package com.frame24.api.operations.api;

import com.frame24.api.common.response.ApiResponse;
import com.frame24.api.common.security.UserPrincipal;
import com.frame24.api.operations.application.dto.CreateShowtimeRequest;
import com.frame24.api.operations.application.dto.ShowtimeDashboardResponse;
import com.frame24.api.operations.application.dto.ShowtimeResponse;
import com.frame24.api.operations.application.dto.UpdateShowtimeRequest;
import com.frame24.api.operations.application.service.ShowtimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/showtimes")
@RequiredArgsConstructor
@Tag(name = "Showtimes", description = "Endpoints para gerenciamento de sessões")
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    @PostMapping(version = "v1.0+")
    @Operation(summary = "Criar sessão")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> create(
            @Valid @RequestBody CreateShowtimeRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ShowtimeResponse response = showtimeService.create(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Sessão criada com sucesso"));
    }

    @GetMapping(version = "v1.0+")
    @Operation(summary = "Listar sessões por complexo e período")
    public ResponseEntity<ApiResponse<List<ShowtimeResponse>>> list(
            @RequestParam @Parameter(description = "ID do complexo de cinema") Long complexId,
            @RequestParam @Parameter(description = "Data de início (ISO 8601)") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @Parameter(description = "Data de término (ISO 8601)") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ShowtimeResponse> response = showtimeService.listByComplexAndDateRange(complexId, start, end, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Sessões listadas com sucesso"));
    }

    @GetMapping(value = "/dashboard", version = "v1.0+")
    @Operation(summary = "Dashboard de sessões com ocupação (via View)")
    public ResponseEntity<ApiResponse<List<ShowtimeDashboardResponse>>> listDashboard(
            @RequestParam @Parameter(description = "ID do complexo de cinema") Long complexId,
            @RequestParam @Parameter(description = "Data de início (ISO 8601)") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant start,
            @RequestParam @Parameter(description = "Data de término (ISO 8601)") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant end,
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ShowtimeDashboardResponse> response = showtimeService.listDashboard(complexId, start, end, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Dashboard carregado com sucesso"));
    }

    @GetMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Buscar sessão por ID")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> getById(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        ShowtimeResponse response = showtimeService.getById(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Sessão encontrada"));
    }

    @PutMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Atualizar sessão")
    public ResponseEntity<ApiResponse<ShowtimeResponse>> update(
            @PathVariable Long id, @Valid @RequestBody UpdateShowtimeRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        ShowtimeResponse response = showtimeService.update(id, request, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Sessão atualizada com sucesso"));
    }

    @DeleteMapping(value = "/{id}", version = "v1.0+")
    @Operation(summary = "Deletar sessão")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        showtimeService.delete(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Sessão deletada com sucesso"));
    }

    @PostMapping(value = "/{id}/cancel", version = "v1.0+")
    @Operation(summary = "Cancelar sessão")
    public ResponseEntity<ApiResponse<Void>> cancel(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        showtimeService.cancel(id, principal);
        return ResponseEntity.ok(ApiResponse.success(null, "Sessão cancelada com sucesso"));
    }

    @GetMapping(value = "/{id}/financials", version = "v1.0+")
    @Operation(summary = "Preview financeiro da sessão")
    public ResponseEntity<ApiResponse<com.frame24.api.operations.application.dto.FinancialPreviewResponse>> getFinancialPreview(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        var response = showtimeService.getFinancialPreview(id, principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Preview financeiro gerado com sucesso"));

    }

    @GetMapping(value = "/{id}/seat-map", version = "v1.0+")
    @Operation(summary = "Mapa de assentos da sessão (com status merged)")
    public ResponseEntity<ApiResponse<List<com.frame24.api.operations.application.dto.SeatMapResponse>>> getSeatMap(
            @PathVariable Long id, @AuthenticationPrincipal UserPrincipal principal) {
        List<com.frame24.api.operations.application.dto.SeatMapResponse> response = showtimeService.getSeatMap(id,
                principal);
        return ResponseEntity.ok(ApiResponse.success(response, "Mapa de assentos carregado com sucesso"));
    }

}
                