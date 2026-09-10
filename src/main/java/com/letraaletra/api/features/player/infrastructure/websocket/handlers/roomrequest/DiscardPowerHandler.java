package com.letraaletra.api.features.player.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.player.application.input.DiscardPowerInput;
import com.letraaletra.api.features.player.application.output.DiscardPowerOutput;
import com.letraaletra.api.features.player.application.port.PlayerNotifier;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.DiscardPowerWsRequest;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.DiscardPowerResponse;
import com.letraaletra.api.features.player.infrastructure.presentation.mapper.DiscardPowerResponseMapper;
import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.slf4j.event.Level;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

@Component
public class DiscardPowerHandler implements RoomRequestHandler<DiscardPowerWsRequest> {
    private final UseCase<DiscardPowerInput, DiscardPowerOutput> discardPowerUseCase;
    private final DiscardPowerResponseMapper discardPowerResponseMapper;
    private final PlayerNotifier playerNotifier;
    private final AuditService auditService;

    public DiscardPowerHandler(
            UseCase<DiscardPowerInput, DiscardPowerOutput> discardPowerUseCase,
            DiscardPowerResponseMapper discardPowerResponseMapper,
            PlayerNotifier playerNotifier,
            AuditService auditService
    ) {
        this.discardPowerUseCase = discardPowerUseCase;
        this.discardPowerResponseMapper = discardPowerResponseMapper;
        this.playerNotifier = playerNotifier;
        this.auditService = auditService;
    }

    @Override
    public void handle(DiscardPowerWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        DiscardPowerInput input = discardPowerResponseMapper.toInput(request, userId);

        DiscardPowerOutput output = discardPowerUseCase.execute(input);

        send(output);

        if (output.events() != null && !output.events().isEmpty()) {
            String gameId = request.gameId();
            String matchLogFileName = null;
            if (output.game() != null && output.game().getGameState() != null && output.game().getGameState().getMatchId() != null) {
                matchLogFileName = output.game().getGameState().getMatchId().toString();
            }
            for (var event : output.events()) {
                if (event.event() != null && event.event().name().equals("TURN_PASSED")) {
                    auditService.game(
                            gameId,
                            matchLogFileName,
                            Level.INFO,
                            "Turno passado automaticamente para jogador {} devido a descarte enquanto congelado sem defesa",
                            event.data() != null ? event.data().toString() : "unknown"
                    );
                }
            }
        }
    }

    private void send(DiscardPowerOutput output) {
        List<Player> players = output.game().getGameState()
                .getPlayers().values()
                .stream().toList();

        UUID eventId = UUID.randomUUID();

        for (Player player : players) {
            DiscardPowerResponse dto = discardPowerResponseMapper.toResponse(output, player.getUserId());

            playerNotifier.notifyUser(player.getUserId(), dto, eventId);
        }
    }

    @Override
    public Class<DiscardPowerWsRequest> getType() {
        return DiscardPowerWsRequest.class;
    }
}
