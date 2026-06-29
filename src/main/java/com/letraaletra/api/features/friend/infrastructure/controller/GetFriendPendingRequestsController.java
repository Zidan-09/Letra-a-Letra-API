package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.GetFriendPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetFriendPendingRequestsOutput;
import com.letraaletra.api.features.friend.application.usecase.GetFriendPendingRequestsUseCase;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.GetFriendPendingRequestsResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.GetFriendPendingRequestsMapper;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/friend")
public class GetFriendPendingRequestsController {
    private final GetFriendPendingRequestsUseCase useCase;

    public GetFriendPendingRequestsController(
            GetFriendPendingRequestsUseCase useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping("/pending")
    public ResponseEntity<SuccessResponse<GetFriendPendingRequestsResponse>> handle(
            @AuthenticationPrincipal User user
    ) {
        GetFriendPendingRequestsInput input = GetFriendPendingRequestsMapper.toInput(user.getId());

        GetFriendPendingRequestsOutput output = useCase.execute(input);

        GetFriendPendingRequestsResponse dto = GetFriendPendingRequestsMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
