package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.EnableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.EnableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.usecase.EnableCosmeticUseCase;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.EnableCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.EnableCosmeticMapper;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cosmetic")
public class EnableCosmeticController {
    private final EnableCosmeticUseCase useCase;

    public EnableCosmeticController(
            EnableCosmeticUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/enable/{cosmeticId}")
    public ResponseEntity<SuccessResponse<EnableCosmeticResponse>> handle(
            @AuthenticationPrincipal User user,
            @PathVariable @NotBlank String cosmeticId
    ) {
        EnableCosmeticInput input = EnableCosmeticMapper.toInput(user, cosmeticId);

        EnableCosmeticOutput output = useCase.execute(input);

        EnableCosmeticResponse dto = EnableCosmeticMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
