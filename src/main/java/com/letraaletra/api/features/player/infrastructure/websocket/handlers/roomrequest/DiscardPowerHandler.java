package com.letraaletra.api.features.player.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.player.application.input.DiscardPowerInput;
import com.letraaletra.api.features.player.application.output.DiscardPowerOutput;
import com.letraaletra.api.features.game.application.port.GameNotifier;
import com.letraaletra.api.features.player.application.usecase.DiscardPowerUseCase;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.DiscardPowerWsRequest;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.response.DiscardPowerResponse;
import com.letraaletra.api.features.player.infrastructure.presentation.mapper.DiscardPowerResponseMapper;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
public class DiscardPowerHandler implements RoomRequestHandler<DiscardPowerWsRequest> {
    private final DiscardPowerUseCase discardPowerUseCase;
    private final DiscardPowerResponseMapper discardPowerResponseMapper;
    private final GameNotifier gameNotifier;

    public DiscardPowerHandler(
            DiscardPowerUseCase discardPowerUseCase,
            DiscardPowerResponseMapper discardPowerResponseMapper,
            GameNotifier gameNotifier
    ) {
        this.discardPowerUseCase = discardPowerUseCase;
        this.discardPowerResponseMapper = discardPowerResponseMapper;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(DiscardPowerWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        DiscardPowerInput input = discardPowerResponseMapper.toInput(request, userId);

        DiscardPowerOutput output = discardPowerUseCase.execute(input);

        send(output);
    }

    private void send(DiscardPowerOutput output) {
        List<Player> players = output.game().getGameState()
                .getPlayers().values()
                .stream().toList();

        for (Player player : players) {
            DiscardPowerResponse dto = discardPowerResponseMapper.toResponse(output, player.getUserId());

            gameNotifier.notifierOne(player.getUserId(), dto);
        }
    }

    @Override
    public Class<DiscardPowerWsRequest> getType() {
        return DiscardPowerWsRequest.class;
    }
}
