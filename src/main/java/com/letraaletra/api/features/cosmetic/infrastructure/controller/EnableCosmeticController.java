package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.EnableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.EnableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.EnableCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.EnableCosmeticMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
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
@RequestMapping("/cosmetic")
@Tag(name = "Cosmetics", description = "Rotas relacionadas ao gerenciamento de cosméticos")
public class EnableCosmeticController {
    private final UseCase<EnableCosmeticInput, EnableCosmeticOutput> useCase;

    public EnableCosmeticController(
            UseCase<EnableCosmeticInput, EnableCosmeticOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/enable/{cosmeticId}")
    public ResponseEntity<SuccessResponse<EnableCosmeticResponse>> handle(
            @AuthenticationPrincipal UUID auth,
            @PathVariable @NotBlank String cosmeticId
    ) {
        EnableCosmeticInput input = EnableCosmeticMapper.toInput(auth, cosmeticId);

        EnableCosmeticOutput output = useCase.execute(input);

        EnableCosmeticResponse dto = EnableCosmeticMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
