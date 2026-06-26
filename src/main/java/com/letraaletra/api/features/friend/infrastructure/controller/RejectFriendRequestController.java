package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.RejectFriendRequestInput;
import com.letraaletra.api.features.friend.application.usecase.RejectFriendRequestUseCase;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.RejectFriendRequestRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.RejectFriendRequestMapper;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friend")
public class RejectFriendRequestController {
    private final RejectFriendRequestUseCase useCase;

    public RejectFriendRequestController(
            RejectFriendRequestUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/reject")
    public ResponseEntity<SuccessResponse<Void>> rejectFriendRequest(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RejectFriendRequestRequest request
            ) {
        RejectFriendRequestInput input = RejectFriendRequestMapper.toInput(user.getId().toString(), request.friendId());

        useCase.execute(input);

        return ApiResponseService.success(null);
    }
}
