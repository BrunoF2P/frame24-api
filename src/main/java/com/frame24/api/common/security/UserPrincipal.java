package com.frame24.api.common.security;

import lombok.Builder;
import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Implementação customizada de UserDetails que inclui contexto adicional
 * necessário para Row Level Security (RLS) e operações multi-tenant.
 *
 * <p>
 * Esta classe armazena as informações do usuário autenticado e fornece
 * os dados necessários para o {@link RLSContextInterceptor} configurar
 * as variáveis de sessão do PostgreSQL.
 * </p>
 *
 * <h3>Tipos de Usuário:</h3>
 * <ul>
 * <li><b>CUSTOMER</b>: Cliente final que compra ingressos (requer
 * customerId)</li>
 * <li><b>EMPLOYEE</b>: Funcionário do ERP com acesso à company (requer
 * companyId)</li>
 * <li><b>SYSTEM</b>: Processos internos do sistema</li>
 * </ul>
 *
 * <h3>Exemplo de Uso:</h3>
 *
 * <pre>
 * // Criar UserPrincipal para funcionário
 * UserPrincipal employee = UserPrincipal.builder()
 *         .userId("emp-123")
 *         .email("joao@empresa.com")
 *         .password("$2a$10$hashedpassword")
 *         .userType("EMPLOYEE")
 *         .companyId("company-abc")
 *         .authorities(List.of(new SimpleGrantedAuthority("ROLE_MANAGER")))
 *         .build();
 *
 * // Criar UserPrincipal para cliente
 * UserPrincipal customer = UserPrincipal.builder()
 *         .userId("user-456")
 *         .email("maria@email.com")
 *         .userType("CUSTOMER")
 *         .companyId("company-abc")
 *         .customerId("customer-789")
 *         .build();
 * </pre>
 *
 * @see RLSContextInterceptor
 * @see UserDetails
 */
@Getter
@Builder
public class UserPrincipal implements UserDetails {

    private final Long userId;

    private final String email;

    private final String password;

    @Builder.Default
    private final String userType = "SYSTEM";

    private final Long companyId;

    private final Long customerId;

    private final Long roleId;

    private final String roleName;

    @Builder.Default
    private final List<Long> allowedComplexes = List.of();

    @Builder.Default
    private final List<GrantedAuthority> authorities = List.of();

    @Builder.Default
    private final boolean enabled = true;

    @Builder.Default
    private final boolean accountNonExpired = true;

    @Builder.Default
    private final boolean credentialsNonExpired = true;

    @Builder.Default
    private final boolean accountNonLocked = true;

    /**
     * Retorna as permissões do usuário.
     *
     * @return Collection de GrantedAuthority
     */
    @NullMarked
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @NullMarked
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean isEmployee() {
        return "EMPLOYEE".equals(userType);
    }

    public boolean isCustomer() {
        return "CUSTOMER".equals(userType);
    }

    public boolean isSystem() {
        return "SYSTEM".equals(userType);
    }
}
