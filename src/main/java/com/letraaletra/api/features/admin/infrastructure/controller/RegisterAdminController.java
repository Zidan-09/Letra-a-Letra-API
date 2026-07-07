package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.RegisterAdminInput;
import com.letraaletra.api.features.admin.application.output.RegisterAdminOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.RegisterAdminRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.RegisterAdminResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.RegisterAdminMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class RegisterAdminController {
    private final UseCase<RegisterAdminInput, RegisterAdminOutput> useCase;

    public RegisterAdminController(
            UseCase<RegisterAdminInput, RegisterAdminOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @PostMapping()
    public ResponseEntity<SuccessResponse<RegisterAdminResponse>> handle(
            @AuthenticationPrincipal UUID auth,
            @Valid @RequestBody RegisterAdminRequest request
    ) {
        RegisterAdminInput input = RegisterAdminMapper.toInput(auth, request);

        RegisterAdminOutput output = useCase.execute(input);

        RegisterAdminResponse dto = RegisterAdminMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
