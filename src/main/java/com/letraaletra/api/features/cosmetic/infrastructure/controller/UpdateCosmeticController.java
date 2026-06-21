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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateCosmeticController {
    private final UpdateCosmeticUseCase useCase;

    public UpdateCosmeticController(
            UpdateCosmeticUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/cosmetic")
    public ResponseEntity<SuccessResponse<UpdateCosmeticResponse>> handle(@Valid @RequestBody UpdateCosmeticRequest request) {
        UpdateCosmeticInput input = UpdateCosmeticMapper.toInput(request);

        UpdateCosmeticOutput output = useCase.execute(input);

        UpdateCosmeticResponse dto = UpdateCosmeticMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
