package com.frame24.api.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "Envelope para respostas paginadas")
public class PaginatedResponse<T> {

    @Schema(description = "Lista de itens da página atual")
    private List<T> content;

    @Schema(description = "Número da página atual (0-indexed)", example = "0")
    private int page;

    @Schema(description = "Tamanho da página", example = "10")
    private int size;

    @Schema(description = "Total de elementos em todas as páginas", example = "100")
    private long totalElements;

    @Schema(description = "Total de páginas disponíveis", example = "10")
    private int totalPages;

    @Schema(description = "Indica se é a última página", example = "false")
    private boolean last;

    public static <T> PaginatedResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return PaginatedResponse.<T>builder()
                .content(content)
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .last(page >= totalPages - 1)
                .build();
    }
}
