package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.domain.actions.GameAction;
import com.letraaletra.api.features.player.domain.actions.UnfreezeAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.UnfreezeActionRequest;
import org.springframework.stereotype.Component;

@Component
public class UnfreezeActionHandler extends AbstractPlayerActionHandler<UnfreezeActionRequest> {
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
