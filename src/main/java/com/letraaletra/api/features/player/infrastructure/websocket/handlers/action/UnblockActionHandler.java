package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.game.domain.board.power.action.RevealCellAction;
import com.letraaletra.api.features.game.domain.board.power.action.UnblockAndRevealAction;
import com.letraaletra.api.features.game.domain.board.power.action.UnblockCellAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.UnblockActionRequest;
import com.letraaletra.api.shared.application.usecase.UseCase;
import org.springframework.stereotype.Component;

@Component
public class UnblockActionHandler extends AbstractPlayerActionHandler<UnblockActionRequest> {
    public UnblockActionHandler(UseCase<PlayerActionInput, PlayerActionOutput> useCase, PlayerNotifier notifier) {
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
