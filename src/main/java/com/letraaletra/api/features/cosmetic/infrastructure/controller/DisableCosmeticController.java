package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.DisableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DisableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.usecase.DisableCosmeticUseCase;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.DisableCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.DisableCosmeticMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/cosmetic")
@Tag(name = "Cosmetics", description = "Rotas relacionadas ao gerenciamento de cosméticos")
public class DisableCosmeticController {
    private final DisableCosmeticUseCase useCase;

    public DisableCosmeticController(
            DisableCosmeticUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/disable/{cosmeticId}")
    public ResponseEntity<SuccessResponse<DisableCosmeticResponse>> handle(
            @AuthenticationPrincipal UUID auth,
            @PathVariable @NotBlank String cosmeticId
    ) {
        DisableCosmeticInput input = DisableCosmeticMapper.toInput(auth, cosmeticId);

        DisableCosmeticOutput output = useCase.execute(input);

        DisableCosmeticResponse dto = DisableCosmeticMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
