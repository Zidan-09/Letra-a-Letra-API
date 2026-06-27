package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.power.domain.actions.DetectTrapsAction;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.DetectTrapsActionRequest;
import org.springframework.stereotype.Component;

@Component
public class DetectTrapsActionHandler extends AbstractPlayerActionHandler<DetectTrapsActionRequest> {

    public DetectTrapsActionHandler(PlayerActionUseCase useCase, GameNotifier notifier) {
        super(useCase, notifier);
    }

    @Override
    protected GameAction createAction(DetectTrapsActionRequest request) {
        return new DetectTrapsAction(
                request.actionId()
        );
    }

    @Override
    public Class<DetectTrapsActionRequest> getType() {
        return DetectTrapsActionRequest.class;
    }
}
