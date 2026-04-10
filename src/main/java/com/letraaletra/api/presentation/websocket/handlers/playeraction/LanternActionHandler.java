package com.letraaletra.api.presentation.websocket.handlers.playeraction;

import com.letraaletra.api.application.command.player.PlayerActionCommand;
import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.usecase.player.PlayerActionUseCase;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.LanternAction;
import com.letraaletra.api.presentation.dto.request.player.LanternActionDTO;
import com.letraaletra.api.presentation.dto.response.websocket.PlayerActionResponseDTO;
import com.letraaletra.api.presentation.mappers.player.PlayerActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class LanternActionHandler implements InGameActionHandler<LanternActionDTO> {
    @Autowired
    private PlayerActionUseCase playerActionUseCase;

    @Autowired
    private PlayerActionMapper playerActionMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(LanternActionDTO request, WebSocketSession session, String tokenGameId) {
        String userId = (String) session.getAttributes().get("userId");

        GameAction action = new LanternAction(request.actionId());

        PlayerActionCommand command = playerActionMapper.toCommand(tokenGameId, userId, action);

        PlayerActionOutput output = playerActionUseCase.execute(command);

        PlayerActionResponseDTO dto = playerActionMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<LanternActionDTO> getType() {
        return LanternActionDTO.class;
    }
}
