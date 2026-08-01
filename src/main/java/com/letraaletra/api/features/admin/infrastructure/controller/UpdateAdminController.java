package com.letraaletra.api.features.admin.infrastructure.controller;

import com.letraaletra.api.features.admin.application.input.UpdateAdminInput;
import com.letraaletra.api.features.admin.application.output.UpdateAdminOutput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.UpdateAdminRequest;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.response.UpdateAdminResponse;
import com.letraaletra.api.features.admin.infrastructure.presentation.mapper.UpdateAdminMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/admin")
@Tag(name = "Admin", description = "Rotas relacionadas a parte de administração")
public class UpdateAdminController {
    private final UseCase<UpdateAdminInput, UpdateAdminOutput> useCase;

    public UpdateAdminController(
            UseCase<UpdateAdminInput, UpdateAdminOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @PutMapping(path = "/{adminId}")
    public synchronized ResponseEntity<SuccessResponse<UpdateAdminResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID adminId,
            @RequestBody @Valid UpdateAdminRequest request
    ) {
            UpdateAdminInput input = UpdateAdminMapper.toInput(principal, adminId, request);

            UpdateAdminOutput output = useCase.execute(input);

            UpdateAdminResponse dto = UpdateAdminMapper.toResponse(output);

            return ApiResponseHandler.success(dto);
    }
}
