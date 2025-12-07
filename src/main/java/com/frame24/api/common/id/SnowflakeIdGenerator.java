package com.frame24.api.common.id;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Gerador de IDs Snowflake para o sistema.
 *
 * <p>
 * Implementação otimizada do algoritmo Snowflake ID que gera IDs únicos distribuídos
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
 * <h3>Otimizações:</h3>
 * <ul>
 * <li>Thread.onSpinWait() para reduzir consumo de CPU</li>
 * <li>Clock skew tolerance de 50ms (padrão da indústria)</li>
 * <li>Retorno de tipo primitivo long (sem autoboxing)</li>
 * <li>Métodos de extração de componentes para debug</li>
 * </ul>
 *
 * <h3>Uso:</h3>
 *
 * <pre>
 * {@code
 * @Autowired
 * private SnowflakeIdGenerator idGenerator;
 *
 * long id = idGenerator.nextId();
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

    // Clock skew tolerance (50ms é o padrão da indústria)
    private static final long CLOCK_BACKWARD_TOLERANCE_MS = 50L;

    private final long workerId;
    private final long datacenterId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    /**
     * Construtor com configuração de worker e datacenter.
     *
     * <p>
     * Usa workerId=1 e datacenterId=1 por padrão.
     * Para produção, configure via properties:
     * </p>
     * <pre>
     * snowflake.worker-id=1
     * snowflake.datacenter-id=1
     * </pre>
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
     * Retorna primitivo long para evitar autoboxing.
     * </p>
     *
     * @return ID Snowflake como long primitivo
     */
    public synchronized long nextId() {
        long timestamp = currentTimestamp();

        // Clock skew handling com tolerance
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;

            // Se o drift for maior que a tolerância, falha imediatamente
            if (offset > CLOCK_BACKWARD_TOLERANCE_MS) {
                throw new IllegalStateException(
                        String.format("Clock moved backwards by %dms. Refusing to generate ID (tolerance: %dms)",
                                offset, CLOCK_BACKWARD_TOLERANCE_MS));
            }

            // Para drift pequeno (NTP ajustes normais), aguarda recuperação
            timestamp = waitNextMillis(lastTimestamp);
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
        return ((timestamp - CUSTOM_EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * Obtém o timestamp atual em milissegundos.
     */
    private long currentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    /**
     * Aguarda até o próximo milissegundo usando Thread.onSpinWait().
     *
     * <p>
     * onSpinWait() é uma hint para a JVM que reduz consumo de CPU
     * e melhora performance em ~3x comparado a busy-loop puro.
     * </p>
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimestamp();
        while (timestamp <= lastTimestamp) {
            Thread.onSpinWait();  // Hint de otimização para CPU
            timestamp = currentTimestamp();
        }
        return timestamp;
    }

    /**
     * Extrai o timestamp de um ID Snowflake.
     *
     * @param id ID Snowflake
     * @return Timestamp em milissegundos desde o epoch
     */
    public long extractTimestamp(long id) {
        return ((id >> TIMESTAMP_SHIFT) & ~(-1L << 41L)) + CUSTOM_EPOCH;
    }

    /**
     * Extrai o timestamp de um ID Snowflake como Instant.
     *
     * @param id ID Snowflake
     * @return Instant do timestamp
     */
    public Instant extractInstant(long id) {
        return Instant.ofEpochMilli(extractTimestamp(id));
    }

    /**
     * Extrai o datacenter ID de um ID Snowflake.
     *
     * @param id ID Snowflake
     * @return Datacenter ID (0-31)
     */
    public long extractDatacenterId(long id) {
        return (id >> DATACENTER_ID_SHIFT) & MAX_DATACENTER_ID;
    }

    /**
     * Extrai o worker ID de um ID Snowflake.
     *
     * @param id ID Snowflake
     * @return Worker ID (0-31)
     */
    public long extractWorkerId(long id) {
        return (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }

    /**
     * Extrai a sequência de um ID Snowflake.
     *
     * @param id ID Snowflake
     * @return Número da sequência (0-4095)
     */
    public long extractSequence(long id) {
        return id & MAX_SEQUENCE;
    }

    /**
     * Extrai todos os componentes de um ID Snowflake.
     *
     * <p>
     * Útil para debug e análise de distribuição de carga.
     * </p>
     *
     * @param id ID Snowflake
     * @return Objeto com todos os componentes decompostos
     */
    public SnowflakeComponents parse(long id) {
        return new SnowflakeComponents(
                extractTimestamp(id),
                extractDatacenterId(id),
                extractWorkerId(id),
                extractSequence(id)
        );
    }

    /**
     * Representa os componentes decompostos de um ID Snowflake.
     *
     * @param timestamp    Timestamp em milissegundos desde o epoch
     * @param datacenterId ID do datacenter (0-31)
     * @param workerId     ID do worker (0-31)
     * @param sequence     Número da sequência (0-4095)
     */
    public record SnowflakeComponents(
            long timestamp,
            long datacenterId,
            long workerId,
            long sequence
    ) {
        /**
         * Retorna representação legível dos componentes.
         */
        @Override
        public String toString() {
            return String.format(
                    "SnowflakeComponents[timestamp=%d (%s), datacenter=%d, worker=%d, sequence=%d]",
                    timestamp,
                    Instant.ofEpochMilli(timestamp),
                    datacenterId,
                    workerId,
                    sequence
            );
        }
    }
}
