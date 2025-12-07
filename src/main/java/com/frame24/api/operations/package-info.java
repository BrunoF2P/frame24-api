/**
 * Módulo de Operações de Cinema.
 * 
 * <p>
 * Responsável por gerenciar complexos de cinema, salas, sessões,
 * assentos e programação de filmes.
 * </p>
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 * <li>Gerenciamento de complexos e salas de cinema</li>
 * <li>Programação de sessões (showtimes)</li>
 * <li>Controle de assentos e disponibilidade</li>
 * <li>Tipos de projeção e áudio</li>
 * </ul>
 * 
 * <h3>API Pública:</h3>
 * <ul>
 * <li>{@code ShowtimeApi} - Interface para consulta de sessões</li>
 * <li>{@code SeatApi} - Interface para gerenciamento de assentos</li>
 * <li>{@code CinemaComplexApi} - Interface para consulta de complexos</li>
 * </ul>
 * 
 * <h3>Eventos Publicados:</h3>
 * <ul>
 * <li>{@code ShowtimeScheduledEvent} - Quando uma sessão é agendada</li>
 * <li>{@code SeatReservedEvent} - Quando um assento é reservado</li>
 * <li>{@code SeatReleasedEvent} - Quando um assento é liberado</li>
 * </ul>
 * 
 * <h3>Dependências:</h3>
 * <ul>
 * <li>{@code common} - Infraestrutura compartilhada</li>
 * <li>{@code identity} - Para validação de empresas</li>
 * <li>{@code catalog} - Para informações de filmes</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(displayName = "Cinema Operations", allowedDependencies = { "common",
        "identity", "catalog" })
package com.frame24.api.operations;
