package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.game.domain.board.power.action.UnfreezeAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.UnfreezeActionRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;

@Component
public class UnfreezeActionHandler extends AbstractPlayerActionHandler<UnfreezeActionRequest> {
    public UnfreezeActionHandler(UseCase<PlayerActionInput, PlayerActionOutput> useCase, PlayerNotifier notifier) {
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
