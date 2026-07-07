package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.GetFriendListInput;
import com.letraaletra.api.features.friend.application.output.GetFriendListOutput;
import com.letraaletra.api.features.friend.application.usecase.GetFriendListUseCase;
import com.letraaletra.api.features.friend.domain.FriendMessages;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.GetFriendListResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.GetFriendListMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/friend")
public class GetFriendListController {
    private final GetFriendListUseCase useCase;

    public GetFriendListController(
            GetFriendListUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<GetFriendListResponse>> getFriends(
            @AuthenticationPrincipal UUID auth
    ) {
        GetFriendListInput input = GetFriendListMapper.toInput(auth.toString());

        GetFriendListOutput output = useCase.execute(input);

        GetFriendListResponse dto = GetFriendListMapper.toResponse(output);

        return ApiResponseService.success(dto, FriendMessages.FRIENDS_FOUND.getMessage());
    }
}
