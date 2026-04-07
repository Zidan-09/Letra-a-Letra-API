package com.letraaletra.api.presentation.websocket.handlers.playeraction;

import com.letraaletra.api.application.command.player.PlayerActionCommand;
import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.application.usecase.player.PlayerActionUseCase;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.TrapCellAction;
import com.letraaletra.api.presentation.dto.request.player.TrapActionDTO;
import com.letraaletra.api.presentation.dto.response.websocket.PlayerActionResponseDTO;
import com.letraaletra.api.presentation.mappers.player.PlayerActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class TrapActionHandler implements InGameActionHandler<TrapActionDTO> {
    @Autowired
    private PlayerActionUseCase playerActionUseCase;

    @Autowired
    private PlayerActionMapper playerActionMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(TrapActionDTO request, WebSocketSession session, String gameTokenId) {
        String userId = (String) session.getAttributes().get("userId");

        Position position = new Position(request.position().x(), request.position().y());
        GameAction gameAction = new TrapCellAction(position, request.actionId());

        PlayerActionCommand command = playerActionMapper.toCommand(gameTokenId, userId, gameAction);

        PlayerActionOutput output = playerActionUseCase.execute(command);

        PlayerActionResponseDTO dto = playerActionMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);
    }

    @Override
    public Class<TrapActionDTO> getType() {
        return TrapActionDTO.class;
    }
}
