package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.game.domain.board.power.action.TrapCellAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.TrapActionRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;

@Component
public class TrapActionHandler extends AbstractPlayerActionHandler<TrapActionRequest> {
    public TrapActionHandler(UseCase<PlayerActionInput, PlayerActionOutput> useCase, PlayerNotifier notifier) {
        super(useCase, notifier);
    }

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
