package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.features.game.domain.board.power.action.DetectTrapsAction;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.DetectTrapsActionRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;

@Component
public class DetectTrapsActionHandler extends AbstractPlayerActionHandler<DetectTrapsActionRequest> {

    public DetectTrapsActionHandler(UseCase<PlayerActionInput, PlayerActionOutput> useCase, PlayerNotifier notifier) {
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
