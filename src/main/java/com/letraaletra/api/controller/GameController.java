package com.letraaletra.api.controller;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.dto.response.SuccessResponse;
import com.letraaletra.api.dto.response.game.GameDTO;
import com.letraaletra.api.exception.messages.GameMessages;
import com.letraaletra.api.service.GameService;
import com.letraaletra.api.utils.server.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<GameDTO>>> getGames() {
        List<GameDTO> result = gameService.getGames();

        return ApiResponse.success(result, GameMessages.GAMES_FOUND.getMessage());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<Game>> getGameById(@RequestParam String id) {
        Game result = gameService.findById(id);

        return ApiResponse.success(result, GameMessages.GAME_FOUND.getMessage());
    }
}
