/**
 * Pacote de geração de IDs.
 * 
 * <p>
 * Contém o gerador de IDs Snowflake e utilitários relacionados
 * para uso em todos os módulos da aplicação.
 * </p>
 * 
 * <h3>Componentes:</h3>
 * <ul>
 * <li>{@link com.frame24.api.common.id.SnowflakeIdGenerator} - Gerador de IDs Snowflake</li>
 * <li>{@link com.frame24.api.common.id.SnowflakeEntityListener} - Listener JPA para geração
 * automática</li>
 * <li>{@link com.frame24.api.common.id.SnowflakeIdConverter} - Conversor JPA</li>
 * </ul>
 * 
 * <h3>Uso Básico:</h3>
 * 
 * <pre>
 * {@code
 * // Injetar o gerador
 * @Autowired
 * private SnowflakeIdGenerator idGenerator;
 * 
 * // Gerar ID
 * Long id = idGenerator.nextId();
 * }
 * </pre>
 * 
 * <h3>Uso com JPA:</h3>
 * 
 * <pre>
 * {
 *     &#64;code
 *     &#64;Entity
 *     &#64;EntityListeners(SnowflakeEntityListener.class)
 *     public class MyEntity {
 *         @Id
 *         private Long id; // Será gerado automaticamente
 *     }
 * }
 * </pre>
 */
package com.frame24.api.common.id;
