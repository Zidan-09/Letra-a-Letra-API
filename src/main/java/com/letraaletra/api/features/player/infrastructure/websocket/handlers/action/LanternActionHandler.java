package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.game.domain.board.power.action.LanternAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.LanternActionRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;

@Component
public class LanternActionHandler extends AbstractPlayerActionHandler<LanternActionRequest> {
    public LanternActionHandler(UseCase<PlayerActionInput, PlayerActionOutput> useCase, PlayerNotifier notifier) {
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
