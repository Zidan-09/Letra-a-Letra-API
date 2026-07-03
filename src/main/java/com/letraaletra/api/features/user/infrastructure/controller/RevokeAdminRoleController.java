package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.RevokeAdminRoleInput;
import com.letraaletra.api.features.user.application.usecase.RevokeAdminRoleUseCase;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.RevokeAdminRoleMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/user")
public class RevokeAdminRoleController {
    private final RevokeAdminRoleUseCase useCase;

    public RevokeAdminRoleController(
            RevokeAdminRoleUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/admin/remove/{userId}")
    public ResponseEntity<SuccessResponse<Void>> handle(
            @AuthenticationPrincipal User user,
            @PathVariable @NotBlank String userId
    ) {
        RevokeAdminRoleInput input = RevokeAdminRoleMapper.toInput(user.getId(), userId);

        useCase.execute(input);

        return ApiResponseService.success(null);
    }
}
