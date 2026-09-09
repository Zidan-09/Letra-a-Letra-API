package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.features.game.domain.board.power.action.FreezePlayerAction;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.FreezeActionRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FreezeActionHandler extends AbstractPlayerActionHandler<FreezeActionRequest> {
    public FreezeActionHandler(UseCase<PlayerActionInput, PlayerActionOutput> useCase, PlayerNotifier notifier) {
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
