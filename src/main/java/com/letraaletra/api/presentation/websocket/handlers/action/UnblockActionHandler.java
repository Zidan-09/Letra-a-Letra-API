package com.letraaletra.api.presentation.websocket.handlers.action;

import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.UnblockCellAction;
import com.letraaletra.api.presentation.dto.request.player.UnblockActionDTO;
import org.springframework.stereotype.Component;

@Component
public class UnblockActionHandler extends AbstractPlayerActionHandler<UnblockActionDTO> {
    @Override
    protected GameAction createAction(UnblockActionDTO request) {
        Position position = new Position(request.position().x(), request.position().y());

        return new UnblockCellAction(
                request.actionId(),
                position
        );
    }

    @Override
    public Class<UnblockActionDTO> getType() {
        return UnblockActionDTO.class;
    }
}
