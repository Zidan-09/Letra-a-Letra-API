package com.letraaletra.api.features.player.infrastructure.websocket.handlers.roomrequest;

import com.letraaletra.api.features.player.application.input.DiscardPowerInput;
import com.letraaletra.api.features.player.application.output.DiscardPowerOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.features.player.application.usecase.DiscardPowerUseCase;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.presentation.dto.request.DiscardPowerWsRequest;
import com.letraaletra.api.presentation.dto.response.websocket.DiscardPowerResponse;
import com.letraaletra.api.features.player.infrastructure.presentation.mapper.DiscardPowerDTOMapper;
import com.letraaletra.api.presentation.websocket.handlers.roomrequest.RoomRequestHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;

@Component
public class DiscardPowerHandler implements RoomRequestHandler<DiscardPowerWsRequest> {
    private final DiscardPowerUseCase discardPowerUseCase;
    private final DiscardPowerDTOMapper discardPowerDTOMapper;
    private final GameNotifier gameNotifier;

    public DiscardPowerHandler(
            DiscardPowerUseCase discardPowerUseCase,
            DiscardPowerDTOMapper discardPowerDTOMapper,
            GameNotifier gameNotifier
    ) {
        this.discardPowerUseCase = discardPowerUseCase;
        this.discardPowerDTOMapper = discardPowerDTOMapper;
        this.gameNotifier = gameNotifier;
    }

    @Override
    public void handle(DiscardPowerWsRequest request, WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");

        DiscardPowerInput command = discardPowerDTOMapper.toCommand(request, userId);

        DiscardPowerOutput output = discardPowerUseCase.execute(command);

        send(output);
    }

    private void send(DiscardPowerOutput output) {
        List<Player> players = output.game().getGameState()
                .getPlayers().values()
                .stream().toList();

        for (Player player : players) {
            DiscardPowerResponse dto = discardPowerDTOMapper.toResponseDTO(output, player.getUserId());

            gameNotifier.notifierOne(player.getUserId(), dto);
        }
    }

    @Override
    public Class<DiscardPowerWsRequest> getType() {
        return DiscardPowerWsRequest.class;
    }
}
