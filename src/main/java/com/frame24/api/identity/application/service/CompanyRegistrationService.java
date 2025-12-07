package com.frame24.api.identity.application.service;

import com.frame24.api.common.exception.ConflictException;
import com.frame24.api.common.exception.ValidationException;
import com.frame24.api.identity.application.dto.CompanyRegistrationRequest;
import com.frame24.api.identity.application.dto.CompanyRegistrationResponse;
import com.frame24.api.identity.domain.*;
import com.frame24.api.identity.domain.enums.IdentityType;
import com.frame24.api.identity.domain.enums.TaxRegimeType;
import com.frame24.api.identity.infrastructure.client.BrasilApiClient;
import com.frame24.api.identity.infrastructure.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Serviço de registro de novas empresas com administrador.
 */
@Service
public class CompanyRegistrationService {

    private static final Logger log = LoggerFactory.getLogger(CompanyRegistrationService.class);
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9-]");

    private final BrasilApiClient brasilApiClient;
    private final CompanyRepository companyRepository;
    private final PersonRepository personRepository;
    private final IdentityRepository identityRepository;
    private final CustomRoleRepository customRoleRepository;
    private final CompanyUserRepository companyUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final DefaultRolesService defaultRolesService;
    private final DefaultPermissionsService defaultPermissionsService;

    public CompanyRegistrationService(
            BrasilApiClient brasilApiClient,
            CompanyRepository companyRepository,
            PersonRepository personRepository,
            IdentityRepository identityRepository,
            CustomRoleRepository customRoleRepository,
            CompanyUserRepository companyUserRepository,
            PasswordEncoder passwordEncoder,
            DefaultRolesService defaultRolesService,
            DefaultPermissionsService defaultPermissionsService) {
        this.brasilApiClient = brasilApiClient;
        this.companyRepository = companyRepository;
        this.personRepository = personRepository;
        this.identityRepository = identityRepository;
        this.customRoleRepository = customRoleRepository;
        this.companyUserRepository = companyUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultRolesService = defaultRolesService;
        this.defaultPermissionsService = defaultPermissionsService;
    }

    /**
     * Registra uma nova empresa com seu administrador.
     *
     * @param request dados de registro
     * @return dados da empresa e admin criados
     */
    @Transactional
    public CompanyRegistrationResponse register(CompanyRegistrationRequest request) {
        log.info("Iniciando registro de empresa: {}", request.corporateName());

        // 1. Validações de unicidade
        validateUniqueness(request);

        // 2. Validar CNPJ na Receita Federal e obter dados
        var cnpjData = fetchAndValidateCnpjData(request.cnpj());
        TaxRegimeType taxRegime = determineTaxRegime(cnpjData);

        // 3. Gerar tenant slug único
        String tenantSlug = generateUniqueSlug(request.tradeName(), request.corporateName());

        // 4. Criar Company
        Company company = createCompany(request, tenantSlug, taxRegime, cnpjData);

        // 5. Sincronizar permissões do plano e Criar roles padrão
        defaultPermissionsService.syncPermissions(company, company.getPlanType());
        defaultRolesService.createDefaultRoles(company);

        // 6. Buscar role Administrador
        CustomRole adminRole = customRoleRepository
                .findByCompanyIdAndName(company.getId(), "Administrador")
                .orElseThrow(() -> new IllegalStateException("Role Administrador não encontrada"));

        // 7. Criar Person (admin)
        Person person = createPerson(request);

        // 8. Criar Identity (admin)
        Identity identity = createIdentity(request, person);

        // 9. Criar CompanyUser
        createCompanyUser(company, identity, adminRole);

        log.info("Empresa registrada com sucesso: {} (slug: {})", company.getCorporateName(), tenantSlug);

        return new CompanyRegistrationResponse(
                company.getId(),
                company.getCorporateName(),
                company.getTradeName(),
                company.getTenantSlug(),
                company.getTaxRegime(),
                company.getPlanType(),
                identity.getId(),
                identity.getEmail());
    }

    private void validateUniqueness(CompanyRegistrationRequest request) {
        String sanitizedCnpj = sanitizeCnpj(request.cnpj());

        if (companyRepository.existsByCnpj(sanitizedCnpj)) {
            throw new ConflictException("Empresa", "CNPJ", request.cnpj());
        }

        if (identityRepository.existsByEmail(request.email())) {
            throw new ConflictException("Usuário", "email", request.email());
        }
    }

    private BrasilApiClient.CnpjResponse fetchAndValidateCnpjData(String cnpj) {
        var cnpjData = brasilApiClient.getCnpjData(cnpj);

        if (cnpjData == null) {
            throw new ValidationException("cnpj", "CNPJ não encontrado na Receita Federal");
        }

        if (!cnpjData.isAtivo()) {
            throw new ValidationException("cnpj", "CNPJ não está ativo na Receita Federal");
        }

        return cnpjData;
    }

    private TaxRegimeType determineTaxRegime(BrasilApiClient.CnpjResponse cnpjData) {
        // Determina regime tributário
        if (cnpjData.isOptanteSimplesNacional()) {
            log.info("CNPJ {} é optante do Simples Nacional", cnpjData.cnpj());
            return TaxRegimeType.SIMPLES_NACIONAL;
        } else {
            log.info("CNPJ {} não é Simples Nacional, usando Lucro Presumido", cnpjData.cnpj());
            return TaxRegimeType.LUCRO_PRESUMIDO;
        }
    }

    private String generateUniqueSlug(String tradeName, String corporateName) {
        String baseName = tradeName != null && !tradeName.isBlank() ? tradeName : corporateName;
        String slug = slugify(baseName);

        // Garante unicidade
        String finalSlug = slug;
        int counter = 1;
        while (companyRepository.existsByTenantSlug(finalSlug)) {
            finalSlug = slug + "-" + counter++;
        }

        return finalSlug;
    }

    private String slugify(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        String slug = NON_ALPHANUMERIC.matcher(normalized.toLowerCase(Locale.ROOT).replace(" ", "-")).replaceAll("");
        return slug.replaceAll("-+", "-").replaceAll("^-|-$", "");
    }

    private Company createCompany(CompanyRegistrationRequest request, String tenantSlug, TaxRegimeType taxRegime,
                                  BrasilApiClient.CnpjResponse cnpjData) {
        Company company = new Company();
        company.setCorporateName(request.corporateName());
        company.setTradeName(request.tradeName());
        company.setCnpj(sanitizeCnpj(request.cnpj()));

        // Usa dados do request OU da API como fallback
        company.setZipCode(firstNonNull(request.companyZipCode(), sanitizeCnpj(cnpjData.cep())));
        company.setStreetAddress(firstNonNull(request.companyStreetAddress(), cnpjData.logradouro()));
        company.setAddressNumber(firstNonNull(request.companyAddressNumber(), cnpjData.numero()));
        company.setAddressComplement(firstNonNull(request.companyAddressComplement(), cnpjData.complemento()));
        company.setNeighborhood(firstNonNull(request.companyNeighborhood(), cnpjData.bairro()));
        company.setCity(firstNonNull(request.companyCity(), cnpjData.municipio()));
        company.setState(firstNonNull(request.companyState(), cnpjData.uf()));

        company.setPhone(request.companyPhone());
        company.setEmail(request.companyEmail());
        company.setTenantSlug(tenantSlug);
        company.setTaxRegime(taxRegime);
        company.setPlanType(request.planType());
        company.setActive(true);
        company.setSuspended(false);
        company.setCreatedAt(Instant.now());

        return companyRepository.save(company);
    }

    private String firstNonNull(String a, String b) {
        return a != null && !a.isBlank() ? a : b;
    }

    private Person createPerson(CompanyRegistrationRequest request) {
        Person person = new Person();
        person.setFullName(request.fullName());
        person.setMobile(request.mobile());
        person.setEmail(request.email());
        person.setCreatedAt(Instant.now());
        person.setUpdatedAt(Instant.now());

        return personRepository.save(person);
    }

    private Identity createIdentity(CompanyRegistrationRequest request, Person person) {
        Identity identity = new Identity();
        identity.setPerson(person);
        identity.setEmail(request.email());
        identity.setPasswordHash(passwordEncoder.encode(request.password()));
        identity.setPasswordChangedAt(Instant.now());
        identity.setIdentityType(IdentityType.EMPLOYEE);
        identity.setActive(true);
        identity.setEmailVerified(false);
        identity.setFailedLoginAttempts(0);
        identity.setLoginCount(0);
        identity.setRequires2fa(false);
        identity.setCreatedAt(Instant.now());

        return identityRepository.save(identity);
    }

    private void createCompanyUser(Company company, Identity identity, CustomRole role) {
        CompanyUser companyUser = new CompanyUser();
        companyUser.setIdentity(identity);
        companyUser.setCompany(company);
        companyUser.setRole(role);
        companyUser.setActive(true);
        companyUser.setStartDate(LocalDate.now());
        companyUser.setAssignedAt(Instant.now());
        companyUser.setAccessCount(0);
        companyUser.setCreatedAt(Instant.now());

        companyUserRepository.save(companyUser);
    }

    private String sanitizeCnpj(String cnpj) {
        return cnpj.replaceAll("\\D", "");
    }
}
