package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.VerifyResetCodeInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.VerifyResetCodeRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.VerifyResetCodeMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class VerifyResetCodeController {
    private final UseCase<VerifyResetCodeInput, Void> useCase;

    @PostMapping(path = "/auth/verify-reset-code")
    public ResponseEntity<SuccessResponse<Void>> handle(@Valid @RequestBody VerifyResetCodeRequest request) {
        VerifyResetCodeInput input = VerifyResetCodeMapper.toInput(request);

        useCase.execute(input);

        return ApiResponseHandler.success(null, HttpStatus.NO_CONTENT);
    }
}
