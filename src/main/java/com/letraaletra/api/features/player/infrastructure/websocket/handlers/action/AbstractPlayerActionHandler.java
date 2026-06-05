package com.letraaletra.api.features.player.infrastructure.websocket.handlers.action;

import com.letraaletra.api.features.player.application.input.PlayerActionInput;
import com.letraaletra.api.features.player.application.output.PlayerActionOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.features.player.application.usecase.PlayerActionUseCase;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.participant.domain.ParticipantRole;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.power.domain.actions.GameAction;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionRequest;
import com.letraaletra.api.presentation.dto.response.websocket.PlayerActionResponse;
import com.letraaletra.api.features.player.infrastructure.presentation.mapper.PlayerActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

public abstract class AbstractPlayerActionHandler<T extends PlayerActionRequest>
        implements InGameActionHandler<T> {

    @Autowired
    protected PlayerActionUseCase playerActionUseCase;

    @Autowired
    protected PlayerActionMapper playerActionMapper;

    @Autowired
    protected GameNotifier gameNotifier;

    @Override
    public void handle(T request, WebSocketSession session, String gameTokenId) {
        String userId = (String) session.getAttributes().get("userId");

        GameAction action = createAction(request);

        PlayerActionInput command =
                playerActionMapper.toCommand(gameTokenId, userId, action);

        PlayerActionOutput output = playerActionUseCase.execute(command);

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
            PlayerActionResponse dto = playerActionMapper.toResponseDTO(output, player.getUserId());

            gameNotifier.notifierOne(player.getUserId(), dto);
        }

        for (Participant spectator : spectators) {
            PlayerActionResponse dto = playerActionMapper.toAllResponseDTO(output);

            gameNotifier.notifierOne(spectator.getUserId(), dto);
        }
    }
}