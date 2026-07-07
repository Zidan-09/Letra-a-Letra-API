package com.letraaletra.api.features.cosmetic.infrastructure.controller;

import com.letraaletra.api.features.cosmetic.application.input.UpdateCosmeticInput;
import com.letraaletra.api.features.cosmetic.application.output.UpdateCosmeticOutput;
import com.letraaletra.api.features.cosmetic.application.usecase.UpdateCosmeticUseCase;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.request.UpdateCosmeticRequest;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.UpdateCosmeticResponse;
import com.letraaletra.api.features.cosmetic.infrastructure.presentation.mapper.UpdateCosmeticMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/cosmetic")
public class UpdateCosmeticController {
    private final UpdateCosmeticUseCase useCase;

    public UpdateCosmeticController(
            UpdateCosmeticUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PutMapping("/{cosmeticId}")
    public ResponseEntity<SuccessResponse<UpdateCosmeticResponse>> handle(
            @AuthenticationPrincipal UUID auth,
            @Valid @RequestBody UpdateCosmeticRequest request,
            @PathVariable @NotBlank String cosmeticId
    ) {
        UpdateCosmeticInput input = UpdateCosmeticMapper.toInput(auth, request, cosmeticId);

        UpdateCosmeticOutput output = useCase.execute(input);

        UpdateCosmeticResponse dto = UpdateCosmeticMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
