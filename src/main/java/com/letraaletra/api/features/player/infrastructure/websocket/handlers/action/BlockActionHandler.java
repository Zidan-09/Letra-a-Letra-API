package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.power.domain.actions.BlockCellAction;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.BlockActionRequest;
import org.springframework.stereotype.Component;

@Component
public class BlockActionHandler extends AbstractPlayerActionHandler<BlockActionRequest> {

    @Override
    protected GameAction createAction(BlockActionRequest request) {
        Position position = new Position(request.position().x(), request.position().y());

        return new BlockCellAction(
                request.actionId(),
                position
        );
    }

    @Override
    public Class<BlockActionRequest> getType() {
        return BlockActionRequest.class;
    }
}
