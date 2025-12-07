/**
 * Módulo de Catálogo de Filmes.
 * 
 * <p>
 * Responsável por gerenciar o catálogo de filmes, distribuidores,
 * classificações e informações relacionadas a filmes.
 * </p>
 * 
 * <h3>Responsabilidades:</h3>
 * <ul>
 * <li>Gerenciamento de filmes e metadados</li>
 * <li>Distribuidores de filmes</li>
 * <li>Classificações indicativas</li>
 * <li>Gêneros e categorias</li>
 * </ul>
 * 
 * <h3>API Pública:</h3>
 * <ul>
 * <li>{@code MovieApi} - Interface para consulta de filmes</li>
 * <li>{@code DistributorApi} - Interface para distribuidores</li>
 * </ul>
 * 
 * <h3>Eventos Publicados:</h3>
 * <ul>
 * <li>{@code MovieAddedEvent} - Quando um filme é adicionado</li>
 * <li>{@code MovieUpdatedEvent} - Quando informações de filme são
 * atualizadas</li>
 * </ul>
 * 
 * <h3>Dependências:</h3>
 * <ul>
 * <li>{@code common} - Infraestrutura compartilhada</li>
 * <li>{@code identity} - Para validação de empresas</li>
 * </ul>
 */
@org.springframework.modulith.ApplicationModule(displayName = "Movie Catalog", allowedDependencies = { "common",
        "identity" })
package com.frame24.api.catalog;
