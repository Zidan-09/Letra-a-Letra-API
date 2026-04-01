package com.letraaletra.api.presentation.controller;

import com.letraaletra.api.application.command.game.FindByTokenCommand;
import com.letraaletra.api.application.usecase.game.FindByCodeUseCase;
import com.letraaletra.api.application.usecase.game.FindByTokenGameIdUseCase;
import com.letraaletra.api.application.usecase.game.GetPublicGamesUseCase;
import com.letraaletra.api.application.output.game.FindByTokenOutput;
import com.letraaletra.api.presentation.dto.response.SuccessResponse;
import com.letraaletra.api.presentation.dto.response.game.FindByTokenResponseDTO;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.domain.game.GameMessages;
import com.letraaletra.api.presentation.mappers.game.FindByTokenMapper;
import com.letraaletra.api.presentation.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/game")
public class GameController {

    @Autowired
    private GetPublicGamesUseCase getGames;

    @Autowired
    private FindByTokenMapper findByTokenMapper;

    @Autowired
    private FindByTokenGameIdUseCase findByTokenGameIdUseCase;

    @Autowired
    private FindByCodeUseCase findByCode;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<GameDTO>>> getGames() {
        List<GameDTO> result = getGames.execute();

        return ApiResponse.success(result, GameMessages.GAMES_FOUND.getMessage());
    }

    @GetMapping("/{tokenGameId}")
    public ResponseEntity<SuccessResponse<FindByTokenResponseDTO>> getGameById(@Valid @PathVariable String tokenGameId) {
        FindByTokenCommand command = findByTokenMapper.toCommand(tokenGameId);

        FindByTokenOutput output = findByTokenGameIdUseCase.execute(command);

        FindByTokenResponseDTO dto = findByTokenMapper.toResponseDTO(output);

        return ApiResponse.success(dto, GameMessages.GAME_FOUND.getMessage());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<SuccessResponse<Optional<GameDTO>>> getGameByCode(@Valid @PathVariable String code) {
        Optional<GameDTO> result = findByCode.execute(code);

        return ApiResponse.success(result);
    }
}
