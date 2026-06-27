package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.ImmunityPlayerAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.ImmunityActionRequest;
import org.springframework.stereotype.Component;

@Component
public class ImmunityActionHandler extends AbstractPlayerActionHandler<ImmunityActionRequest> {
    public ImmunityActionHandler(PlayerActionUseCase useCase, GameNotifier notifier) {
        super(useCase, notifier);
    }

    @Override
    protected GameAction createAction(ImmunityActionRequest request) {
        return new ImmunityPlayerAction(
                request.actionId()
        );
    }

    @Override
    public Class<ImmunityActionRequest> getType() {
        return ImmunityActionRequest.class;
    }
}
