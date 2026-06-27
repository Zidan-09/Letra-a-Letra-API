package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionRequest;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.PlayerActionResponse;
import com.letraaletra.api.features.player.infrastructure.presentation.mapper.PlayerActionMapper;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

public abstract class AbstractPlayerActionHandler<T extends PlayerActionRequest>
        implements InGameActionHandler<T> {

    protected final PlayerActionUseCase useCase;
    protected final GameNotifier notifier;

    public AbstractPlayerActionHandler(
            PlayerActionUseCase useCase,
            GameNotifier notifier
    ) {
        this.useCase = useCase;
        this.notifier = notifier;
    }

    @Override
    public void handle(T request, WebSocketSession session, String gameId) {
        UUID userId = UUID.fromString((String) session.getAttributes().get("userId"));

        GameAction action = createAction(request);

        PlayerActionInput input =
                PlayerActionMapper.toInput(gameId, userId, action);

        PlayerActionOutput output = useCase.execute(input);

        send(output);

        afterHandle(output);
    }

    protected abstract GameAction createAction(T request);

    protected void afterHandle(PlayerActionOutput output) {
    }

    private void send(PlayerActionOutput output) {
        List<Player> players = output.game().getGameState()
                .getPlayers().values()
                .stream().toList();

        List<Participant> spectators = output.game().getParticipants().stream()
                .filter(participant -> participant.getRole().equals(ParticipantRole.SPECTATOR))
                .toList();

        for (Player player : players) {
            PlayerActionResponse dto = PlayerActionMapper.toResponse(output, player.getUserId());

            notifier.notifierOne(player.getUserId(), dto);
        }

        for (Participant spectator : spectators) {
            PlayerActionResponse dto = PlayerActionMapper.toGlobalResponse(output);

            notifier.notifierOne(spectator.getUserId(), dto);
        }
    }
}