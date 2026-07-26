package com.letraaletra.api.features.game.infrastructure.controller;

import com.letraaletra.api.features.game.application.input.GetActiveGamesInput;
import com.letraaletra.api.features.game.application.output.GetActiveGamesOutput;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GetActiveGamesMapper;
import com.letraaletra.api.shared.infrastructure.presentation.dto.handlers.ApiResponseHandler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "game")
@Tag(name = "Game", description = "Rotas relacionadas a funcionalidade de salas")
public class GetActiveGamesController {
    private final UseCase<GetActiveGamesInput, GetActiveGamesOutput> useCase;

    public GetActiveGamesController(
            UseCase<GetActiveGamesInput, GetActiveGamesOutput> useCase
    ) {
        this.useCase = useCase;
    }

    @GetMapping(path = "/active")
    public ResponseEntity<SuccessResponse<PageResponse<GameDTO>>> handle(
            @AuthenticationPrincipal AuthenticatedUser principal,
            Pageable pageable
    ) {
        GetActiveGamesInput input = GetActiveGamesMapper.toInput(principal, pageable);

        GetActiveGamesOutput output = useCase.execute(input);

        PageResponse<GameDTO> dto = GetActiveGamesMapper.toResponse(output);

        return ApiResponseHandler.success(dto);
    }
}
