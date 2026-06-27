package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.UnfreezeAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.UnfreezeActionRequest;
import org.springframework.stereotype.Component;

@Component
public class UnfreezeActionHandler extends AbstractPlayerActionHandler<UnfreezeActionRequest> {
    public UnfreezeActionHandler(PlayerActionUseCase useCase, GameNotifier notifier) {
        super(useCase, notifier);
    }

    @Override
    protected GameAction createAction(UnfreezeActionRequest request) {
        return new UnfreezeAction(
                request.actionId()
        );
    }

    @Override
    public Class<UnfreezeActionRequest> getType() {
        return UnfreezeActionRequest.class;
    }
}
