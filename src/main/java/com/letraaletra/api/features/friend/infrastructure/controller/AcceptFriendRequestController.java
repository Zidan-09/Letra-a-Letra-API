package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.AcceptFriendRequestInput;
import com.letraaletra.api.features.friend.application.usecase.AcceptFriendRequestUseCase;
import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.AcceptFriendRequestRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.AcceptFriendRequestMapper;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
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
public class AcceptFriendRequestController {
    private final AcceptFriendRequestUseCase useCase;

    public AcceptFriendRequestController(
            AcceptFriendRequestUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/accept")
    public ResponseEntity<SuccessResponse<Void>> acceptFriendRequest(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AcceptFriendRequestRequest request
    ) {
        AcceptFriendRequestInput input = AcceptFriendRequestMapper.toInput(user.getId(), request.friendId());

        useCase.execute(input);

        return ApiResponseService.success(null, FriendMessages.REQUEST_ACCEPTED.getMessage(), HttpStatus.NO_CONTENT);
    }
}
