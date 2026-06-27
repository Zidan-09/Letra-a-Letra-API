package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.power.domain.actions.BlindPlayerAction;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.BlindActionRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BlindActionHandler extends AbstractPlayerActionHandler<BlindActionRequest> {

    public BlindActionHandler(PlayerActionUseCase useCase, GameNotifier notifier) {
        super(useCase, notifier);
    }

    @Override
    protected GameAction createAction(BlindActionRequest request) {
        return new BlindPlayerAction(
                request.actionId(),
                UUID.fromString(request.targetId())
        );
    }
    @Override
    public Class<BlindActionRequest> getType() {
        return BlindActionRequest.class;
    }
}
