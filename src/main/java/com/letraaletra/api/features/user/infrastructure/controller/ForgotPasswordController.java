package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.ForgotPasswordInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.ForgotPasswordRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.ForgotPasswordMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class ForgotPasswordController {
    private final UseCase<ForgotPasswordInput, Void> useCase;

    @Transactional
    @PostMapping(path = "/auth/forgot-password")
    public ResponseEntity<SuccessResponse<Void>> handle(@Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordInput input = ForgotPasswordMapper.toInput(request);

        useCase.execute(input);

        return ApiResponseHandler.success(null, HttpStatus.NO_CONTENT);
    }
}
