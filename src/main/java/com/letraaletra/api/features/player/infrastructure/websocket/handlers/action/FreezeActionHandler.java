package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.power.domain.actions.FreezePlayerAction;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.FreezeActionRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FreezeActionHandler extends AbstractPlayerActionHandler<FreezeActionRequest> {
    public FreezeActionHandler(PlayerActionUseCase useCase, GameNotifier notifier) {
        super(useCase, notifier);
    }

    @Override
    protected GameAction createAction(FreezeActionRequest request) {
        return new FreezePlayerAction(
                request.actionId(),
                UUID.fromString(request.targetId())
        );
    }

    @Override
    public Class<FreezeActionRequest> getType() {
        return FreezeActionRequest.class;
    }
}
