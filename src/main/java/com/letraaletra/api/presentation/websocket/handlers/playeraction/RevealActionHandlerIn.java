package com.letraaletra.api.presentation.websocket.handlers.playeraction;

import com.letraaletra.api.application.usecase.player.PlayerActionUseCase;
import com.letraaletra.api.domain.game.actions.GameAction;
import com.letraaletra.api.domain.game.actions.RevealCellAction;
import com.letraaletra.api.domain.position.Position;
import com.letraaletra.api.presentation.dto.request.websocket.playeractions.RevealActionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class RevealActionHandlerIn implements InGameActionHandler<RevealActionDTO> {

    @Autowired
    private PlayerActionUseCase playerActionUseCase;

    @Override
    public void handle(RevealActionDTO request, WebSocketSession session, String gameTokenId) {
        Position position = new Position(request.position().x(), request.position().y());
        GameAction gameAction = new RevealCellAction(position);

        playerActionUseCase.execute(
                gameTokenId,
                (String) session.getAttributes().get("userId"),
                gameAction
        );
    }

    @Override
    public Class<RevealActionDTO> getType() {
        return RevealActionDTO.class;
    }
}
