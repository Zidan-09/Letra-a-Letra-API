package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.GetFriendPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetFriendPendingRequestsOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.GetFriendPendingRequestsResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.GetFriendPendingRequestsMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(path = "/friend")
@Tag(name = "Friend", description = "Rotas relacionadas a funcionalidade de amizades")
public class GetFriendPendingRequestsController {
    private final UseCase<GetFriendPendingRequestsInput, GetFriendPendingRequestsOutput> useCase;

    public GetFriendPendingRequestsController(
            UseCase<GetFriendPendingRequestsInput, GetFriendPendingRequestsOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping("/pending")
    public ResponseEntity<SuccessResponse<GetFriendPendingRequestsResponse>> handle(
            @AuthenticationPrincipal UUID auth
    ) {
        GetFriendPendingRequestsInput input = GetFriendPendingRequestsMapper.toInput(auth);

        GetFriendPendingRequestsOutput output = useCase.execute(input);

        GetFriendPendingRequestsResponse dto = GetFriendPendingRequestsMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
