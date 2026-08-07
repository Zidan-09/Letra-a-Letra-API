package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.RevokeUserCosmeticInput;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.RevokeUserCosmeticMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class RevokeUserCosmeticController {
    private final UseCase<RevokeUserCosmeticInput, Void> useCase;

    @Transactional
    @DeleteMapping(path = "/{userId}/inventory/{cosmeticId}")
    public ResponseEntity<SuccessResponse<Void>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID userId,
            @PathVariable UUID cosmeticId
    ) {
        RevokeUserCosmeticInput input = RevokeUserCosmeticMapper.toInput(principal, userId, cosmeticId);

        useCase.execute(input);

        return ApiResponseHandler.success(null, HttpStatus.NO_CONTENT);
    }
}
