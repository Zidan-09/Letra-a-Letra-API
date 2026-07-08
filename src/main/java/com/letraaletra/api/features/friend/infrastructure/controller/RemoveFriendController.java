package com.letraaletra.api.features.friend.infrastructure.controller;

import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.request.RemoveFriendRequest;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.RemoveFriendMapper;
import com.letraaletra.api.shared.application.service.ApiResponseService;
import com.letraaletra.api.shared.application.usecase.UseCase;
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
@RequestMapping(path = "/friend")
@Tag(name = "Friend", description = "Rotas relacionadas a funcionalidade de amizades")
public class RemoveFriendController {
    private final UseCase<RemoveFriendInput, Void> useCase;

    public RemoveFriendController(
            UseCase<RemoveFriendInput, Void> useCase
    ) {
        this.useCase = useCase;
    }

    @PatchMapping("/remove")
    public ResponseEntity<SuccessResponse<Void>> removeFriend(
            @AuthenticationPrincipal UUID auth,
            @Valid @RequestBody RemoveFriendRequest request
    ) {
        RemoveFriendInput input = RemoveFriendMapper.toInput(auth.toString(), request.friendId());

        useCase.execute(input);

        return ApiResponseService.success(null);
    }
}
