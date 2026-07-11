package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.AcceptFriendRequestInput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.AcceptFriendRequestRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.AcceptFriendRequestMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friend")
@Tag(name = "Friend", description = "Rotas relacionadas a funcionalidade de amizades")
public class AcceptFriendRequestController {
    private final UseCase<AcceptFriendRequestInput, Void> useCase;

    public AcceptFriendRequestController(
            UseCase<AcceptFriendRequestInput, Void> useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/accept")
    public ResponseEntity<SuccessResponse<Void>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @Valid @RequestBody AcceptFriendRequestRequest request
    ) {
        AcceptFriendRequestInput input = AcceptFriendRequestMapper.toInput(principal.auth(), request.friendId());

        useCase.execute(input);

        return ApiResponseService.success(null, HttpStatus.NO_CONTENT);
    }
}
