package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.user.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.features.user.application.usecase.ChangeCosmeticUseCase;
import com.letraaletra.api.features.user.domain.UserMessages;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.ChangeCosmeticRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.ChangeCosmeticResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.ChangeCosmeticMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/user")
public class ChangeCosmeticController {
    private final ChangeCosmeticUseCase changeCosmeticUseCase;

    public ChangeCosmeticController(ChangeCosmeticUseCase changeCosmeticUseCase) {
        this.changeCosmeticUseCase = changeCosmeticUseCase;
    }

    @PatchMapping(path = "/cosmetic/{userId}")
    public ResponseEntity<SuccessResponse<ChangeCosmeticResponse>> changeCosmetic(@Valid @RequestBody ChangeCosmeticRequest request, @PathVariable @NotBlank String userId) {
        ChangeCosmeticInput input = ChangeCosmeticMapper.toInput(request, userId);

        ChangeCosmeticOutput output = changeCosmeticUseCase.execute(input);

        ChangeCosmeticResponse dto = ChangeCosmeticMapper.toResponse(output);

        return ApiResponseService.success(dto, UserMessages.COSMETIC_EQUIPPED.getMessage());
    }
}
