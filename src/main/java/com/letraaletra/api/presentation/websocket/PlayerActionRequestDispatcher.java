package com.letraaletra.api.presentation.websocket;

import com.letraaletra.api.domain.position.Position;
import com.letraaletra.api.presentation.dto.request.websocket.playeractions.*;
import com.letraaletra.api.domain.game.exceptions.InvalidPlayerActionException;
import com.letraaletra.api.application.game.usecase.PlayerActionUseCase;
import com.letraaletra.api.domain.game.actions.GameAction;
import com.letraaletra.api.domain.game.actions.RevealCellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class PlayerActionRequestDispatcher {
    @Autowired
    private PlayerActionUseCase playerActionUseCase;

    public void dispatch(String gameTokenId, PlayerActionDTO request, WebSocketSession session) {
        switch (request) {
            case RevealActionDTO reveal -> handleReveal(reveal, session, gameTokenId);
            case BlockActionDTO block -> handleBlock(block, session, gameTokenId);
            case UnblockActionDTO unblock -> handleUnblock(unblock, session, gameTokenId);
            case TrapActionDTO trap -> handleTrap(trap, session, gameTokenId);
            case DetectTrapsActionDTO detect -> handleDetectTraps(detect, session, gameTokenId);
            case SpyActionDTO spy -> handleSpy(spy, session, gameTokenId);
            case FreezeActionDTO freeze -> handleFreeze(freeze, session, gameTokenId);
            case UnfreezeActionDTO unfreeze -> handleUnfreeze(unfreeze, session, gameTokenId);
            case BlindActionDTO blind -> handleBlind(blind, session, gameTokenId);
            case LanternActionDTO lantern -> handleLantern(lantern, session, gameTokenId);
            case ImmunityActionDTO immunity -> handleImmunity(immunity, session, gameTokenId);
            default -> throw new InvalidPlayerActionException();
        }
    }

    private void handleReveal(RevealActionDTO request, WebSocketSession session, String gameTokenId) {
        Position position = new Position(request.position().getX(), request.position().getY());
        GameAction gameAction = new RevealCellAction(position);

        playerActionUseCase.execute(
                gameTokenId,
                (String) session.getAttributes().get("userId"),
                gameAction
        );
    }

    private void handleBlock(BlockActionDTO request, WebSocketSession session, String gameTokenId) {
        Position position = new Position(request.position().getX(), request.position().getY());

    }

    private void handleUnblock(UnblockActionDTO request, WebSocketSession session, String gameTokenId) {
        Position position = new Position(request.position().getX(), request.position().getY());
    }

    private void handleTrap(TrapActionDTO request, WebSocketSession session, String gameTokenId) {
        Position position = new Position(request.position().getX(), request.position().getY());
    }

    private void handleDetectTraps(DetectTrapsActionDTO request, WebSocketSession session, String gameTokenId) {

    }

    private void handleSpy(SpyActionDTO request, WebSocketSession session, String gameTokenId) {
        Position position = new Position(request.position().getX(), request.position().getY());
    }

    private void handleFreeze(FreezeActionDTO request, WebSocketSession session, String gameTokenId) {

    }

    private void handleUnfreeze(UnfreezeActionDTO request, WebSocketSession session, String gameTokenId) {

    }

    private void handleBlind(BlindActionDTO request, WebSocketSession session, String gameTokenId) {

    }

    private void handleLantern(LanternActionDTO request, WebSocketSession session, String gameTokenId) {

    }

    private void handleImmunity(ImmunityActionDTO request, WebSocketSession session, String gameTokenId) {

    }
}
