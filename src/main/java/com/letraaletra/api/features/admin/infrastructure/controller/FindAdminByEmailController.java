package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.FindAdminByEmailInput;
import com.letraaletra.api.features.admin.application.output.FindAdminByEmailOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.FindAdminByEmailResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.FindAdminByEmailMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class FindAdminByEmailController {
    private final UseCase<FindAdminByEmailInput, FindAdminByEmailOutput> useCase;

    @GetMapping(path = "/email/{email}")
    public ResponseEntity<SuccessResponse<FindAdminByEmailResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable @NotBlank String email
    ) {
        FindAdminByEmailInput input = FindAdminByEmailMapper.toInput(principal, email);

        FindAdminByEmailOutput output = useCase.execute(input);

        FindAdminByEmailResponse dto = FindAdminByEmailMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
