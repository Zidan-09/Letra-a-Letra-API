package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.CancelFriendRequestInput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.CancelFriendRequestRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.CancelFriendRequestMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friend")
@Tag(name = "Friend", description = "Rotas relacionadas a funcionalidade de amizades")
public class CancelFriendRequestController {
    private final UseCase<CancelFriendRequestInput, Void> useCase;

    @PatchMapping(path = "/cancel")
    public ResponseEntity<SuccessResponse<Void>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody CancelFriendRequestRequest request
    ) {
        CancelFriendRequestInput input = CancelFriendRequestMapper.toInput(principal.auth(), request.friendId());

        useCase.execute(input);

        return ApiResponseHandler.success(null);
    }
}
