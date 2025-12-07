package com.frame24.api.identity.application.service;

import com.frame24.api.identity.application.dto.CompanyRegistrationRequest;
import com.frame24.api.identity.domain.Company;
import com.frame24.api.identity.domain.CustomRole;
import com.frame24.api.identity.domain.Identity;
import com.frame24.api.identity.domain.Person;
import com.frame24.api.identity.domain.enums.CompanyPlanType;
import com.frame24.api.identity.domain.enums.TaxRegimeType;
import com.frame24.api.identity.infrastructure.client.BrasilApiClient;
import com.frame24.api.identity.infrastructure.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyRegistrationServiceTest {

        @Mock
        private BrasilApiClient brasilApiClient;
        @Mock
        private CompanyRepository companyRepository;
        @Mock
        private PersonRepository personRepository;
        @Mock
        private IdentityRepository identityRepository;
        @Mock
        private CustomRoleRepository customRoleRepository;
        @Mock
        private CompanyUserRepository companyUserRepository;
        @Mock
        private PasswordEncoder passwordEncoder;
        @Mock
        private DefaultRolesService defaultRolesService;

        @Mock
        private DefaultPermissionsService defaultPermissionsService;

        private CompanyRegistrationService companyRegistrationService;

        @BeforeEach
        void setUp() {
                companyRegistrationService = new CompanyRegistrationService(
                                brasilApiClient,
                                companyRepository,
                                personRepository,
                                identityRepository,
                                customRoleRepository,
                                companyUserRepository,
                                passwordEncoder,
                                defaultRolesService,
                                defaultPermissionsService);
        }

        @Test
        @DisplayName("Should register company with auto-filled address from BrasilAPI")
        void shouldRegisterCompanyWithAutoFilledAddress() {
                // Arrange
                String cnpj = "59945840000170";
                CompanyRegistrationRequest request = new CompanyRegistrationRequest(
                                "Cinema Estrela LTDA",
                                "CineEstrela",
                                cnpj,
                                null, "", null, null, null, null, null, // Empty address fields
                                "(11) 3333-4444",
                                "company@test.com",
                                "Admin Name",
                                "admin@test.com",
                                "Password123",
                                "(11) 99999-9999",
                                CompanyPlanType.BASIC);

                BrasilApiClient.CnpjResponse cnpjResponse = new BrasilApiClient.CnpjResponse(
                                cnpj, "Cinema Estrela LTDA", "CineEstrela", false, "PORTE", "NATUREZA", "ATIVA",
                                "ATIVA",
                                "AVENIDA PAULISTA", "1000", "SALA 1", "BELA VISTA", "01310-100", "SAO PAULO", "SP");

                when(brasilApiClient.getCnpjData(anyString())).thenReturn(cnpjResponse);
                when(companyRepository.existsByCnpj(any())).thenReturn(false);
                when(identityRepository.existsByEmail(any())).thenReturn(false);
                when(companyRepository.existsByTenantSlug(anyString())).thenReturn(false);

                Company mockCompany = new Company();
                mockCompany.setId(1L);
                mockCompany.setCorporateName("Cinema Estrela LTDA");
                mockCompany.setTenantSlug("cineestrela");
                when(companyRepository.save(any(Company.class))).thenReturn(mockCompany);

                when(customRoleRepository.findByCompanyIdAndName(any(), anyString()))
                                .thenReturn(Optional.of(new CustomRole()));

                when(personRepository.save(any(Person.class))).thenReturn(new Person());
                when(identityRepository.save(any(Identity.class))).thenReturn(new Identity());

                // Act
                companyRegistrationService.register(request);

                // Assert
                ArgumentCaptor<Company> companyCaptor = ArgumentCaptor.forClass(Company.class);
                verify(companyRepository).save(companyCaptor.capture());
                Company savedCompany = companyCaptor.getValue();

                assertEquals("AVENIDA PAULISTA", savedCompany.getStreetAddress());
                assertEquals("1000", savedCompany.getAddressNumber());
                assertEquals("SAO PAULO", savedCompany.getCity());
                assertEquals("SP", savedCompany.getState());
                assertEquals(TaxRegimeType.LUCRO_PRESUMIDO, savedCompany.getTaxRegime());
        }
}
