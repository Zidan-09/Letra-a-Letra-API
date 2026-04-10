package com.letraaletra.api.presentation.websocket.handlers.playeraction;

import com.letraaletra.api.application.command.player.PlayerActionCommand;
import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.usecase.player.PlayerActionUseCase;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.SpyCellAction;
import com.letraaletra.api.presentation.dto.request.player.SpyActionDTO;
import com.letraaletra.api.presentation.dto.response.websocket.PlayerActionResponseDTO;
import com.letraaletra.api.presentation.mappers.player.PlayerActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class SpyActionHandler implements InGameActionHandler<SpyActionDTO> {
    @Autowired
    private PlayerActionUseCase playerActionUseCase;

    @Autowired
    private PlayerActionMapper playerActionMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(SpyActionDTO request, WebSocketSession session, String tokenGameId) {
        String userId = (String) session.getAttributes().get("userId");

        Position position = new Position(request.position().x(), request.position().y());
        GameAction action = new SpyCellAction(request.actionId(), position);

        PlayerActionCommand command = playerActionMapper.toCommand(tokenGameId, userId, action);

        PlayerActionOutput output = playerActionUseCase.execute(command);

        PlayerActionResponseDTO dto = playerActionMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<SpyActionDTO> getType() {
        return SpyActionDTO.class;
    }
}
