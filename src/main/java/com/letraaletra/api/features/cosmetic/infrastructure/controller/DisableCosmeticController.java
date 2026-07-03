package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.DisableCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.DisableCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.usecase.DisableCosmeticUseCase;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.DisableCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.DisableCosmeticMapper;
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
@RequestMapping(path = "/cosmetic")
public class DisableCosmeticController {
    private final DisableCosmeticUseCase useCase;

    public DisableCosmeticController(
            DisableCosmeticUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/disable/{cosmeticId}")
    public ResponseEntity<SuccessResponse<DisableCosmeticResponse>> handle(
            @AuthenticationPrincipal User user,
            @PathVariable @NotBlank String cosmeticId
    ) {
        DisableCosmeticInput input = DisableCosmeticMapper.toInput(user, cosmeticId);

        DisableCosmeticOutput output = useCase.execute(input);

        DisableCosmeticResponse dto = DisableCosmeticMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
