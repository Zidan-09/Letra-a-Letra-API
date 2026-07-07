package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.AuthAdminInput;
import com.letraaletra.api.features.admin.application.output.AuthAdminOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.AuthAdminRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.AuthAdminResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.AuthAdminMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class AuthAdminController {
    private final UseCase<AuthAdminInput, AuthAdminOutput> useCase;

    public AuthAdminController(
            UseCase<AuthAdminInput, AuthAdminOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @PostMapping(path = "/auth")
    public ResponseEntity<SuccessResponse<AuthAdminResponse>> handle(@Valid @RequestBody AuthAdminRequest request) {
        AuthAdminInput input = AuthAdminMapper.toInput(request);

        AuthAdminOutput output = useCase.execute(input);

        AuthAdminResponse dto = AuthAdminMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
