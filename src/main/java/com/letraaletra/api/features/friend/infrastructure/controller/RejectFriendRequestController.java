package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.RejectFriendRequestInput;
import com.letraaletra.api.features.friend.application.usecase.RejectFriendRequestUseCase;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.RejectFriendRequestRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.RejectFriendRequestMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/friend")
@Tag(name = "Friend", description = "Rotas relacionadas a funcionalidade de amizades")
public class RejectFriendRequestController {
    private final RejectFriendRequestUseCase useCase;

    public RejectFriendRequestController(
            RejectFriendRequestUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping(path = "/reject")
    public ResponseEntity<SuccessResponse<Void>> rejectFriendRequest(
            @AuthenticationPrincipal UUID auth,
            @Valid @RequestBody RejectFriendRequestRequest request
            ) {
        RejectFriendRequestInput input = RejectFriendRequestMapper.toInput(auth.toString(), request.friendId());

        useCase.execute(input);

        return ApiResponseService.success(null);
    }
}
