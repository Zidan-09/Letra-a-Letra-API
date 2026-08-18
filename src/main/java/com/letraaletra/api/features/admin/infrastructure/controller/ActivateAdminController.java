package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.ActivateAccountInput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.ActivateAccountRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.ActivateAccountMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class ActivateAdminController {
    private final UseCase<ActivateAccountInput, Void> useCase;

    @Transactional
    @PatchMapping(path = "/activate")
    public ResponseEntity<SuccessResponse<Void>> handle(
            @RequestParam String token,
            @RequestBody @Valid ActivateAccountRequest request
    ) {
        ActivateAccountInput input = ActivateAccountMapper.toInput(token, request);

        useCase.execute(input);

        return ApiResponseHandler.success(null);
    }
}
