package com.letraaletra.api.infra.websocket;

import com.letraaletra.api.dto.request.websocket.playeractions.PlayerAction;
import com.letraaletra.api.dto.request.websocket.playeractions.RevealAction;
import com.letraaletra.api.exception.exceptions.InvalidPlayerActionException;
import com.letraaletra.api.service.PlayerActionService;
import com.letraaletra.api.service.actions.GameAction;
import com.letraaletra.api.service.actions.RevealCellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class PlayerActionRequestDispatcher {
    @Autowired
    private PlayerActionService playerActionService;

    public void dispatch(String gameTokenId, PlayerAction request, WebSocketSession session) {
        switch (request) {
            case RevealAction reveal -> handleReveal(reveal, session, gameTokenId);
            default -> throw new InvalidPlayerActionException();
        }
    }

    private void handleReveal(RevealAction request, WebSocketSession session, String gameTokenId) {
        GameAction gameAction = new RevealCellAction(request.position());

        playerActionService.execute(
                gameTokenId,
                (String) session.getAttributes().get("userId"),
                gameAction
        );
    }
}
