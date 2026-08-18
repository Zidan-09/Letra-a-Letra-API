package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.DeleteAdminInput;
import com.letraaletra.api.features.admin.application.output.DeleteAdminOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.DeleteAdminResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.DeleteAdminMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class DeleteAdminController {
    private final UseCase<DeleteAdminInput, DeleteAdminOutput> useCase;

    @Transactional
    @DeleteMapping(path = "/{adminId}")
    public ResponseEntity<SuccessResponse<DeleteAdminResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID adminId
    ) {
        DeleteAdminInput input = DeleteAdminMapper.toInput(principal, adminId);

        DeleteAdminOutput output = useCase.execute(input);

        DeleteAdminResponse dto = DeleteAdminMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
