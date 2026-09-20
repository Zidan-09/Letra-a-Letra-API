package com.letraaletra.api.features.game.infrastructure.controller;

import com.letraaletra.api.features.game.application.input.FindActiveGameByRoomNameInput;
import com.letraaletra.api.features.game.application.output.FindActiveGameByRoomNameOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameResponse;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.FindActiveGameByRoomNameMapper;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/game")
@Tag(name = "Game", description = "Rotas relacionadas a funcionalidade de salas")
public class FindActiveGameByRoomNameController {
    private final UseCase<FindActiveGameByRoomNameInput, FindActiveGameByRoomNameOutput> useCase;

    @GetMapping(path = "/active/room-name/{roomName}")
    public ResponseEntity<SuccessResponse<PageResponse<GameResponse>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            @PathVariable @NotBlank String roomName,
            Pageable pageable
    ) {
        FindActiveGameByRoomNameInput input = FindActiveGameByRoomNameMapper.toInput(principal, roomName, pageable);

        FindActiveGameByRoomNameOutput output = useCase.execute(input);

        PageResponse<GameResponse> dto = FindActiveGameByRoomNameMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
