package com.letraaletra.api.features.user.infrastructure.controller;

import com.letraaletra.api.features.user.application.input.BanUserInput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.BanUserRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.mapper.BanUserMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/user")
@Tag(name = "User", description = "Rotas relacionadas a funcionalidade de usuários (jogadores)")
public class BanUserController {
    private final UseCase<BanUserInput, Void> useCase;

    @PatchMapping(path = "/{userId}/ban")
    public ResponseEntity<SuccessResponse<Void>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable UUID userId,
            @Valid @RequestBody BanUserRequest request
    ) {
        BanUserInput input = BanUserMapper.toInput(principal, userId, request);

        useCase.execute(input);

        return ApiResponseHandler.success(null, HttpStatus.NO_CONTENT);
    }
}
