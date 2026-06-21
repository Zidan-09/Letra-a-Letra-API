package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;
import com.letraaletra.api.features.friend.application.usecase.RemoveFriendUseCase;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.RemoveFriendRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.RemoveFriendMapper;
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
@RequestMapping(path = "/friend")
public class RemoveFriendController {
    private final RemoveFriendUseCase useCase;

    public RemoveFriendController(
            RemoveFriendUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping("/remove")
    public ResponseEntity<SuccessResponse<Void>> removeFriend(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RemoveFriendRequest request
    ) {
        RemoveFriendInput input = RemoveFriendMapper.toInput(user.getId(), request.friendId());

        useCase.execute(input);

        return ApiResponseService.success(null);
    }
}
