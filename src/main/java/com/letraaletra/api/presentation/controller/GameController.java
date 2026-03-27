package com.letraaletra.api.presentation.controller;

import com.letraaletra.api.application.game.usecase.FindByTokenGameIdUseCase;
import com.letraaletra.api.application.game.usecase.GetPublicGamesUseCase;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.presentation.messages.GameMessages;
import com.letraaletra.api.presentation.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/game")
public class GameController {

    @Autowired
    private GetPublicGamesUseCase getGames;

    @Autowired
    private FindByTokenGameIdUseCase findByTokenGameId;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<GameDTO>>> getGames() {
        List<GameDTO> result = getGames.execute();

        return ApiResponse.success(result, GameMessages.GAMES_FOUND.getMessage());
    }

    @GetMapping("/{tokenGameId}")
    public ResponseEntity<SuccessResponse<Game>> getGameById(@RequestParam String tokenGameId) {
        Game result = findByTokenGameId.execute(tokenGameId);

        return ApiResponse.success(result, GameMessages.GAME_FOUND.getMessage());
    }
}
