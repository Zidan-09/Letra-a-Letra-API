package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.game.domain.board.power.action.BlockCellAction;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.BlockActionRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;

@Component
public class BlockActionHandler extends AbstractPlayerActionHandler<BlockActionRequest> {

    public BlockActionHandler(UseCase<PlayerActionInput, PlayerActionOutput> useCase, PlayerNotifier notifier) {
        super(useCase, notifier);
    }

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
