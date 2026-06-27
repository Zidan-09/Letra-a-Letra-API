package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.SpyCellAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.SpyActionRequest;
import org.springframework.stereotype.Component;

@Component
public class SpyActionHandler extends AbstractPlayerActionHandler<SpyActionRequest> {

    public SpyActionHandler(PlayerActionUseCase useCase, GameNotifier notifier) {
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