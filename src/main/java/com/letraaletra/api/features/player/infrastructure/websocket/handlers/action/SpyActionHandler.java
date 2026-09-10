package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.game.domain.board.power.action.SpyCellAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.SpyActionRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;

@Component
public class SpyActionHandler extends AbstractPlayerActionHandler<SpyActionRequest> {

    public SpyActionHandler(UseCase<PlayerActionInput, PlayerActionOutput> useCase, PlayerNotifier notifier) {
        super(useCase, notifier);
    }

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