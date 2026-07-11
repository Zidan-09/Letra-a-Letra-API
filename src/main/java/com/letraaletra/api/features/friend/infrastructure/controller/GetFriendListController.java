package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.GetFriendListInput;
import com.letraaletra.api.features.friend.application.output.GetFriendListOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.GetFriendListResponse;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.GetFriendListMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/friend")
@Tag(name = "Friend", description = "Rotas relacionadas a funcionalidade de amizades")
public class GetFriendListController {
    private final UseCase<GetFriendListInput, GetFriendListOutput> useCase;

    public GetFriendListController(
            UseCase<GetFriendListInput, GetFriendListOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<GetFriendListResponse>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal
    ) {
        GetFriendListInput input = GetFriendListMapper.toInput(principal.auth());

        GetFriendListOutput output = useCase.execute(input);

        GetFriendListResponse dto = GetFriendListMapper.toResponse(output);

        return ApiResponseService.success(dto);
    }
}
