package com.letraaletra.api.shared.application.port;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.service.GameOverResult;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

public interface GameResponseAssembler {
    WsResponse assembleGameOver(Game game, GameOverResult gameOverResult);
}