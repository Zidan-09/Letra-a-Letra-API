package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.features.game.domain.board.power.action.BlindPlayerAction;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.BlindActionRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class BlindActionHandler extends AbstractPlayerActionHandler<BlindActionRequest> {

    public BlindActionHandler(UseCase<PlayerActionInput, PlayerActionOutput> useCase, PlayerNotifier notifier) {
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
