package com.letraaletra.api.shared.infrastructure.presentation.dto.assembler;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOver;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

public interface GameResponseAssembler {
    WsResponse assembleGameOver(Game game, GameOver gameOver);
}