package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.RevokeSessionInput;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.RefreshSessionMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class LogoutController {
    private final UseCase<RevokeSessionInput, Void> useCase;

    @PostMapping(path = "/auth/logout")
    public ResponseEntity<SuccessResponse<Void>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        RevokeSessionInput input = RefreshSessionMapper.toLogoutInput(principal);

        useCase.execute(input);

        return ApiResponseHandler.success(null, HttpStatus.NO_CONTENT);
    }
}
