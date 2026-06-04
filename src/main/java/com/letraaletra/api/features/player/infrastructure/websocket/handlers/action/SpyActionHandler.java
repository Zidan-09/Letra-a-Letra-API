package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.features.player.domain.actions.GameAction;
import com.letraaletra.api.features.player.domain.actions.SpyCellAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.SpyActionRequest;
import org.springframework.stereotype.Component;

@Component
public class SpyActionHandler extends AbstractPlayerActionHandler<SpyActionRequest> {

    @Override
    protected GameAction createAction(SpyActionRequest request) {
        return new SpyCellAction(
                request.actionId(),
                new Position(request.position().x(), request.position().y())
        );
    }

    @Override
    public Class<SpyActionRequest> getType() {
        return SpyActionRequest.class;
    }
}