package com.frame24.api.identity.infrastructure.repository;

import com.frame24.api.identity.domain.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository para histórico de senhas.
 */
@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistory, Long> {

    /**
     * Busca as últimas N senhas de um usuário, ordenadas da mais recente para a
     * mais antiga.
     *
     * @param identityId ID do identity
     * @return Lista das últimas 3 senhas
     */
    List<PasswordHistory> findTop3ByIdentity_IdOrderByCreatedAtDesc(Long identityId);

    /**
     * Conta quantas senhas existem no histórico de um usuário.
     *
     * @param identityId ID do identity
     * @return Quantidade de registros
     */
    long countByIdentity_Id(Long identityId);
}
