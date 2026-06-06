package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.TrapCellAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.TrapActionRequest;
import org.springframework.stereotype.Component;

@Component
public class TrapActionHandler extends AbstractPlayerActionHandler<TrapActionRequest> {
    @Override
    protected GameAction createAction(TrapActionRequest request) {
        Position position = new Position(request.position().x(), request.position().y());

        return new TrapCellAction(
                request.actionId(),
                position
        );
    }

    @Override
    public Class<TrapActionRequest> getType() {
        return TrapActionRequest.class;
    }
}
