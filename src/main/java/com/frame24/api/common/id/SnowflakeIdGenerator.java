package com.frame24.api.common.id;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Gerador de IDs Snowflake para o sistema.
 *
 * <p>
 * Implementação do algoritmo Snowflake ID que gera IDs únicos distribuídos
 * de 64 bits compostos por:
 * </p>
 * <ul>
 * <li>41 bits: timestamp em milissegundos</li>
 * <li>10 bits: worker ID (datacenter + machine)</li>
 * <li>12 bits: sequência</li>
 * </ul>
 *
 * <p>
 * Os IDs gerados são:
 * </p>
 * <ul>
 * <li>Únicos globalmente</li>
 * <li>Ordenáveis por tempo</li>
 * <li>Gerados sem coordenação central</li>
 * <li>Compatíveis com VARCHAR/TEXT no PostgreSQL</li>
 * </ul>
 *
 * <h3>Uso:</h3>
 *
 * <pre>
 * {@code
 * @Autowired
 * private SnowflakeIdGenerator idGenerator;
 *
 * Long id = idGenerator.nextId();
 * }
 * </pre>
 */
@Component
public class SnowflakeIdGenerator {

    // Epoch customizado (01/01/2024 00:00:00 UTC)
    private static final long CUSTOM_EPOCH = 1704067200000L;

    // Bits de cada componente
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    // Valores máximos
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // Shifts
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long workerId;
    private final long datacenterId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * Construtor padrão.
     * Construtor com configuração de worker e datacenter.
     *
     * <p>
     * Usa workerId=1 e datacenterId=1 por padrão.
     * Para produção, configure via properties.
     * </p>
     */
    public SnowflakeIdGenerator(
            @Value("${snowflake.worker-id:1}") long workerId,
            @Value("${snowflake.datacenter-id:1}") long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("Worker ID deve estar entre 0 e %d", MAX_WORKER_ID));
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("Datacenter ID deve estar entre 0 e %d", MAX_DATACENTER_ID));
        }

        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * Gera o próximo ID Snowflake.
     *
     * <p>
     * Thread-safe e garante unicidade mesmo em alta concorrência.
     * </p>
     *
     * @return ID Snowflake como Long
     */
    public synchronized Long nextId() {
        long timestamp = currentTimestamp();

        // Relógio voltou para trás
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(
                    String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                            lastTimestamp - timestamp));
        }

        // Mesmo milissegundo - incrementa sequência
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;

            // Sequência esgotada - aguarda próximo milissegundo
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // Novo milissegundo - reseta sequência
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // Gera o ID
        long id = ((timestamp - CUSTOM_EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;

        return id;
    }

    /**
     * Obtém o timestamp atual em milissegundos.
     */
    private long currentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Aguarda até o próximo milissegundo.
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimestamp();
        }
        return timestamp;
    }

    /**
     * Extrai o timestamp de um ID Snowflake.
     *
     * @param id ID Snowflake
     * @return Timestamp em milissegundos
     */
    public long extractTimestamp(Long id) {
        return ((id >> TIMESTAMP_SHIFT) & ~(-1L << 41L)) + CUSTOM_EPOCH;
    }

    /**
     * Extrai o timestamp de um ID Snowflake como Instant.
     *
     * @param id ID Snowflake
     * @return Instant do timestamp
     */
    public Instant extractInstant(Long id) {
        return Instant.ofEpochMilli(extractTimestamp(id));
    }
}
