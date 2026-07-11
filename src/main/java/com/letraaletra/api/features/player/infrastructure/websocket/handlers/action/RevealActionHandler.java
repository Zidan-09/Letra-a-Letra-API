package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.power.domain.actions.RevealCellAction;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.RevealActionRequest;
import com.letraaletra.api.shared.application.port.GameResponseAssembler;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;
import org.springframework.stereotype.Component;

@Component
public class RevealActionHandler extends AbstractPlayerActionHandler<RevealActionRequest> {
    private final GameResponseAssembler gameResponseAssembler;

    public RevealActionHandler(
            PlayerActionUseCase useCase,
            GameNotifier notifier,
            GameResponseAssembler gameResponseAssembler
    ) {
        super(useCase, notifier);
        this.gameResponseAssembler = gameResponseAssembler;
    }

    @Override
    protected GameAction createAction(RevealActionRequest request) {
        return new RevealCellAction(
                new Position(request.position().x(), request.position().y())
        );
    }

    @Override
    protected void afterHandle(PlayerActionOutput output) {
        output.gameOver().ifPresent(gameOver -> {
            WsResponse dto = gameResponseAssembler.assembleGameOver(output.game(), gameOver);

            notifier.notifierGameOver(output.game(), dto);
        });
    }

    @Override
    public Class<RevealActionRequest> getType() {
        return RevealActionRequest.class;
    }
}