package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.domain.actions.BlindPlayerAction;
import com.letraaletra.api.features.player.domain.actions.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.BlindActionRequest;
import org.springframework.stereotype.Component;

@Component
public class BlindActionHandler extends AbstractPlayerActionHandler<BlindActionRequest> {

    @Override
    protected GameAction createAction(BlindActionRequest request) {
        return new BlindPlayerAction(
                request.actionId(),
                request.targetId()
        );
    }
    @Override
    public Class<BlindActionRequest> getType() {
        return BlindActionRequest.class;
    }
}
