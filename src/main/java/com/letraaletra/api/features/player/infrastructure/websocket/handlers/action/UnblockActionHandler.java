package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.features.player.domain.actions.GameAction;
import com.letraaletra.api.features.player.domain.actions.RevealCellAction;
import com.letraaletra.api.features.player.domain.actions.UnblockAndRevealAction;
import com.letraaletra.api.features.player.domain.actions.UnblockCellAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.UnblockActionRequest;
import org.springframework.stereotype.Component;

@Component
public class UnblockActionHandler extends AbstractPlayerActionHandler<UnblockActionRequest> {
    @Override
    protected GameAction createAction(UnblockActionRequest request) {
        Position position = new Position(request.position().x(), request.position().y());

        UnblockCellAction unblockCellAction = new UnblockCellAction(
                request.actionId(),
                position
        );

        RevealCellAction revealCellAction = new RevealCellAction(
                position
        );

        return new UnblockAndRevealAction(
                unblockCellAction,
                revealCellAction
        );
    }

    @Override
    public Class<UnblockActionRequest> getType() {
        return UnblockActionRequest.class;
    }
}
