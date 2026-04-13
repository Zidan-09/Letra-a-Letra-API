package com.letraaletra.api.presentation.websocket.handlers.playeraction;

import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.RevealCellAction;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.presentation.dto.request.player.RevealActionDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameOverResponseDTO;
import com.letraaletra.api.presentation.mappers.game.GameOverMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RevealActionHandler extends AbstractPlayerActionHandler<RevealActionDTO> {

    @Autowired
    private GameOverMapper gameOverMapper;

    @Override
    protected GameAction createAction(RevealActionDTO request) {
        return new RevealCellAction(
                new Position(request.position().x(), request.position().y())
        );
    }

    @Override
    protected void afterHandle(PlayerActionOutput output) {
        output.gameOver().ifPresent(gameOver -> {
            GameOverResponseDTO dto =
                    gameOverMapper.toResponseDTO(gameOver, output.game());

            gameNotifier.notifierAll(output.game(), dto);
        });
    }

    @Override
    public Class<RevealActionDTO> getType() {
        return RevealActionDTO.class;
    }
}