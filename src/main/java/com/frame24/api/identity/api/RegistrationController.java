package com.frame24.api.identity.api;

import com.frame24.api.common.exception.ApiErrorResponse;
import com.frame24.api.identity.application.dto.CompanyRegistrationRequest;
import com.frame24.api.identity.application.dto.CompanyRegistrationResponse;
import com.frame24.api.identity.application.service.CompanyRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para registro de novas empresas.
 * <p>
 * Suporta versionamento via header:
 * - Header: api-version: v1.0
 */
@RestController
@RequestMapping
@Tag(name = "Registro", description = "Operações de registro de empresas")
public class RegistrationController {

    private final CompanyRegistrationService registrationService;

    public RegistrationController(CompanyRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping(value = "/register", version = "v1.0+")
    @Operation(summary = "Registrar nova empresa", description = """
            Registra uma nova empresa com seu administrador inicial.
            
            O CNPJ é validado na Receita Federal via BrasilAPI.
            O regime tributário é detectado automaticamente:
            - Optante Simples Nacional → SIMPLES_NACIONAL
            - Caso contrário → LUCRO_PRESUMIDO
            
            São criadas automaticamente 4 roles padrão:
            - Administrador (nível 2)
            - Gerente (nível 3)
            - Operador (nível 4)
            - Visualizador (nível 5)
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Empresa registrada com sucesso", content = @Content(schema = @Schema(implementation = CompanyRegistrationResponse.class))),
            @ApiResponse(responseCode = "409", description = "CNPJ ou email já cadastrado", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "Falha na comunicação com BrasilAPI", content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<CompanyRegistrationResponse> register(
            @Valid @RequestBody CompanyRegistrationRequest request) {

        CompanyRegistrationResponse response = registrationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
