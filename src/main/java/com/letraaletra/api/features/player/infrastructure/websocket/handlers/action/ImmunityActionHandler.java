package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.ImmunityPlayerAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.ImmunityActionRequest;
import org.springframework.stereotype.Component;

@Component
public class ImmunityActionHandler extends AbstractPlayerActionHandler<ImmunityActionRequest> {
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
