package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.power.domain.actions.FreezePlayerAction;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.FreezeActionRequest;
import org.springframework.stereotype.Component;

@Component
public class FreezeActionHandler extends AbstractPlayerActionHandler<FreezeActionRequest> {
    @Override
    protected GameAction createAction(FreezeActionRequest request) {
        return new FreezePlayerAction(
                request.actionId(),
                request.targetId()
        );
    }

    @Override
    public Class<FreezeActionRequest> getType() {
        return FreezeActionRequest.class;
    }
}
