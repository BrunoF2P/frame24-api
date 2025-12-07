package com.frame24.api.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Serviço de manutenção automática de partições de tabelas.
 *
 * <p>
 * Executa automaticamente todo dia 1 de cada mês às 2h da manhã para criar
 * partições do próximo mês nas tabelas particionadas:
 * </p>
 * <ul>
 * <li>sales.sales</li>
 * <li>sales.tickets</li>
 * <li>audit.audit_logs</li>
 * </ul>
 *
 * <p>
 * A função PostgreSQL {@code create_next_month_partitions()} é responsável
 * pela criação efetiva das partições.
 * </p>
 */
@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class PartitionMaintenanceConfig {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Cria partições do próximo mês automaticamente.
     *
     * <p>
     * Cron: 0 0 2 1 * ? = Todo dia 1 de cada mês às 2h da manhã
     * </p>
     *
     * <p>
     * Exemplo: Se executado em 01/12/2024, cria partições para 01/2025
     * </p>
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void createNextMonthPartitions() {
        try {
            log.info("Starting automatic partition creation for next month...");

            // Executar função PostgreSQL
            jdbcTemplate.query(
                    "SELECT * FROM public.create_next_month_partitions()",
                    (rs, rowNum) -> {
                        String table = rs.getString("partition_created");
                        String partition = rs.getString("partition_table");
                        log.info("Created partition: {}.{}", table, partition);
                        return partition;
                    });

            log.info("Partition creation completed successfully");

        } catch (Exception e) {
            log.error("Failed to create next month partitions", e);
            // Não lançar exceção para não quebrar a aplicação
            // Tentar novamente no próximo mês
        }
    }

    /**
     * Método para criar partições manualmente (útil para testes).
     *
     * <p>
     * Pode ser chamado via endpoint de admin ou console.
     * </p>
     */
    public void createPartitionsManually() {
        log.info("Manual partition creation triggered");
        createNextMonthPartitions();
    }
}
