package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.game.domain.board.power.action.GameAction;
import com.letraaletra.api.features.game.domain.board.power.action.RevealCellAction;
import com.letraaletra.api.features.game.domain.board.position.Position;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.RevealActionRequest;
import com.letraaletra.api.features.game.infrastructure.websocket.assembler.GameResponseAssembler;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RevealActionHandler extends AbstractPlayerActionHandler<RevealActionRequest> {
    private final GameResponseAssembler gameResponseAssembler;

    public RevealActionHandler(
            UseCase<PlayerActionInput, PlayerActionOutput> useCase,
            PlayerNotifier notifier,
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
            WsResponse dto = gameResponseAssembler.assembleGameOver(
                    output.game(),
                    gameOver,
                    output.handledGameOver()
            );

            List<String> socketIds = output.game().getParticipants().getParticipants().stream()
                    .map(Participant::getSocketId)
                    .toList();

            notifier.notifyAll(socketIds, dto);
        });
    }

    @Override
    public Class<RevealActionRequest> getType() {
        return RevealActionRequest.class;
    }
}