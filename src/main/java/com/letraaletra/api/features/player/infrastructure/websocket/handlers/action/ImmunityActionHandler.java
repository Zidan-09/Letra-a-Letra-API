package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.game.domain.board.power.action.ImmunityPlayerAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.ImmunityActionRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;

@Component
public class ImmunityActionHandler extends AbstractPlayerActionHandler<ImmunityActionRequest> {
    public ImmunityActionHandler(UseCase<PlayerActionInput, PlayerActionOutput> useCase, PlayerNotifier notifier) {
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
