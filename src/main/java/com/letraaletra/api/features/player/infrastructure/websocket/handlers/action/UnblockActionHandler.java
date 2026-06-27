package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.RevealCellAction;
import com.letraaletra.api.features.power.domain.actions.UnblockAndRevealAction;
import com.letraaletra.api.features.power.domain.actions.UnblockCellAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.UnblockActionRequest;
import org.springframework.stereotype.Component;

@Component
public class UnblockActionHandler extends AbstractPlayerActionHandler<UnblockActionRequest> {
    public UnblockActionHandler(PlayerActionUseCase useCase, GameNotifier notifier) {
        super(useCase, notifier);
    }

    @Override
    protected GameAction createAction(UnblockActionRequest request) {
        Position position = new Position(request.position().x(), request.position().y());

        UnblockCellAction unblockCellAction = new UnblockCellAction(
                request.actionId(),
                position
        );

        RevealCellAction revealCellAction = new RevealCellAction(
                position
        );

        return new UnblockAndRevealAction(
                unblockCellAction,
                revealCellAction
        );
    }

    @Override
    public Class<UnblockActionRequest> getType() {
        return UnblockActionRequest.class;
    }
}
