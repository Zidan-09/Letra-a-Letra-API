package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.VerifyResetTokenInput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.VerifyResetTokenRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.VerifyResetTokenMapper;
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
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class VerifyResetTokenController {
    private final UseCase<VerifyResetTokenInput, Void> useCase;

    @Transactional
    @PostMapping(path = "/auth/verify-reset-token")
    public ResponseEntity<SuccessResponse<Void>> handle(@Valid @RequestBody VerifyResetTokenRequest request) {
        VerifyResetTokenInput input = VerifyResetTokenMapper.toInput(request);

        useCase.execute(input);

        return ApiResponseHandler.success(null, HttpStatus.NO_CONTENT);
    }
}
