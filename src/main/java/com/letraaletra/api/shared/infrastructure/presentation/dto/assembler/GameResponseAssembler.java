package com.letraaletra.api.shared.infrastructure.presentation.dto.assembler;

import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

public interface GameResponseAssembler {
    WsResponse assembleGameOver(Game game, GameOver gameOver, HandledGameOver handledGameOver);
}
