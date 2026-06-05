package com.letraaletra.api.presentation.controller;

import com.letraaletra.api.features.game.application.input.FindByCodeInput;
import com.letraaletra.api.features.game.application.input.FindByTokenInput;
import com.letraaletra.api.features.game.application.output.FindByCodeOutput;
import com.letraaletra.api.features.game.application.output.GetGamesOutput;
import com.letraaletra.api.features.game.application.usecase.FindByCodeUseCase;
import com.letraaletra.api.features.game.application.usecase.FindByTokenGameIdUseCase;
import com.letraaletra.api.features.game.application.usecase.GetPublicGamesUseCase;
import com.letraaletra.api.features.game.application.output.FindByTokenOutput;
import com.letraaletra.api.presentation.dto.response.http.SuccessResponse;
import com.letraaletra.api.presentation.dto.response.http.FindByCodeResponseDTO;
import com.letraaletra.api.presentation.dto.response.http.FindByTokenResponseDTO;
import com.letraaletra.api.features.game.domain.GameMessages;
import com.letraaletra.api.presentation.dto.response.game.GetGamesResponseDTO;
import com.letraaletra.api.presentation.mappers.game.FindByCodeMapper;
import com.letraaletra.api.presentation.mappers.game.FindByTokenMapper;
import com.letraaletra.api.presentation.mappers.game.GetGamesMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/game")
public class GameController {

    @Autowired
    private GetPublicGamesUseCase getPublicGamesUseCase;

    @Autowired
    private GetGamesMapper getGamesMapper;

    @Autowired
    private FindByTokenMapper findByTokenMapper;

    @Autowired
    private FindByCodeMapper findByCodeMapper;

    @Autowired
    private FindByTokenGameIdUseCase findByTokenGameIdUseCase;

    @Autowired
    private FindByCodeUseCase findByCode;

    @GetMapping
    public ResponseEntity<SuccessResponse<GetGamesResponseDTO>> getGames() {
        GetGamesOutput output = getPublicGamesUseCase.execute();

        GetGamesResponseDTO dto = getGamesMapper.toResponseDTO(output);

        return ApiResponse.success(dto, GameMessages.GAMES_FOUND.getMessage());
    }

    @GetMapping("/{tokenGameId}")
    public ResponseEntity<SuccessResponse<FindByTokenResponseDTO>> getGameById(@Valid @PathVariable String tokenGameId) {
        FindByTokenInput command = findByTokenMapper.toCommand(tokenGameId);

        FindByTokenOutput output = findByTokenGameIdUseCase.execute(command);

        FindByTokenResponseDTO dto = findByTokenMapper.toResponseDTO(output);

        return ApiResponse.success(dto, GameMessages.GAME_FOUND.getMessage());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<SuccessResponse<FindByCodeResponseDTO>> getGameByCode(@Valid @PathVariable String code) {
        FindByCodeInput command = findByCodeMapper.toCommand(code);

        FindByCodeOutput output = findByCode.execute(command);

        FindByCodeResponseDTO dto = findByCodeMapper.toResponseDTO(output);

        return ApiResponse.success(dto);
    }
}
