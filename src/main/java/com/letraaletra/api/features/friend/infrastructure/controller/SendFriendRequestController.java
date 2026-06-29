package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.application.usecase.SendFriendRequestUseCase;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.SendFriendRequestRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.SendFriendRequestResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.SendFriendRequestMapper;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friend")
public class SendFriendRequestController {
    private final SendFriendRequestUseCase useCase;

    public SendFriendRequestController(
            SendFriendRequestUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PostMapping(path = "/request")
    public ResponseEntity<SuccessResponse<SendFriendRequestResponse>> sendFriendRequest(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SendFriendRequestRequest request
    ) {
        SendFriendRequestInput input = SendFriendRequestMapper.toInput(user, request.friendId());

        SendFriendRequestOutput output = useCase.execute(input);

        SendFriendRequestResponse dto = SendFriendRequestMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
