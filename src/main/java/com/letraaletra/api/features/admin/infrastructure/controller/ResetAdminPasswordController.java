package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.ResetAdminPasswordInput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.ResetAdminPasswordRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.ResetAdminPasswordMapper;
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
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class ResetAdminPasswordController {
    private final UseCase<ResetAdminPasswordInput, Void> useCase;

    @PostMapping(path = "/auth/reset-password")
    public ResponseEntity<SuccessResponse<Void>> handle(@Valid @RequestBody ResetAdminPasswordRequest request) {
        ResetAdminPasswordInput input = ResetAdminPasswordMapper.toInput(request);

        useCase.execute(input);

        return ApiResponseHandler.success(null, HttpStatus.NO_CONTENT);
    }
}
