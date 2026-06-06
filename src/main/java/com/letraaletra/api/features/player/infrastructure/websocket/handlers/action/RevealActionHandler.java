package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.RevealCellAction;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.RevealActionRequest;
import org.springframework.stereotype.Component;

@Component
public class RevealActionHandler extends AbstractPlayerActionHandler<RevealActionRequest> {

    @Override
    protected GameAction createAction(RevealActionRequest request) {
        return new RevealCellAction(
                new Position(request.position().x(), request.position().y())
        );
    }

    @Override
    protected void afterHandle(PlayerActionOutput output) {
        output.gameOver().ifPresent(
                gameOver -> gameNotifier.notifierGameOver(output.game(), gameOver)
        );
    }

    @Override
    public Class<RevealActionRequest> getType() {
        return RevealActionRequest.class;
    }
}