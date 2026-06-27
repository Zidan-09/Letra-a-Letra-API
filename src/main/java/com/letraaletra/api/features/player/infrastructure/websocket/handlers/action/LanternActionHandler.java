package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.LanternAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.LanternActionRequest;
import org.springframework.stereotype.Component;

@Component
public class LanternActionHandler extends AbstractPlayerActionHandler<LanternActionRequest> {
    public LanternActionHandler(PlayerActionUseCase useCase, GameNotifier notifier) {
        super(useCase, notifier);
    }

    @Override
    protected GameAction createAction(LanternActionRequest request) {
        return new LanternAction(
                request.actionId()
        );
    }

    @Override
    public Class<LanternActionRequest> getType() {
        return LanternActionRequest.class;
    }
}
