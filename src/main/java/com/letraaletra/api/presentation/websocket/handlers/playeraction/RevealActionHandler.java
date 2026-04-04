package com.letraaletra.api.presentation.websocket.handlers.playeraction;

import com.letraaletra.api.application.command.player.PlayerActionCommand;
import com.letraaletra.api.application.output.player.PlayerActionOutput;
import com.letraaletra.api.application.usecase.player.PlayerActionUseCase;
import com.letraaletra.api.application.port.GameNotifier;
import com.letraaletra.api.domain.game.player.actions.GameAction;
import com.letraaletra.api.domain.game.player.actions.RevealCellAction;
import com.letraaletra.api.domain.game.board.position.Position;
import com.letraaletra.api.presentation.dto.request.player.RevealActionDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameOverResponseDTO;
import com.letraaletra.api.presentation.dto.response.websocket.PlayerActionResponseDTO;
import com.letraaletra.api.presentation.mappers.game.GameOverMapper;
import com.letraaletra.api.presentation.mappers.player.PlayerActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class RevealActionHandler implements InGameActionHandler<RevealActionDTO> {

    @Autowired
    private PlayerActionUseCase playerActionUseCase;

    @Autowired
    private PlayerActionMapper playerActionMapper;

    @Autowired
    private GameOverMapper gameOverMapper;

    @Autowired
    private GameNotifier gameNotifier;

    @Override
    public void handle(RevealActionDTO request, WebSocketSession session, String gameTokenId) {
        String userId = (String) session.getAttributes().get("userId");

        Position position = new Position(request.position().x(), request.position().y());
        GameAction gameAction = new RevealCellAction(position);

        PlayerActionCommand command = playerActionMapper.toCommand(gameTokenId, userId, gameAction);

        PlayerActionOutput output = playerActionUseCase.execute(command);

        PlayerActionResponseDTO dto = playerActionMapper.toResponseDTO(output);

        gameNotifier.notifierAll(output.game(), dto);

        if (output.gameOver().isPresent()) {
            GameOverResponseDTO overDto = gameOverMapper.toResponseDTO(output.gameOver().get(), output.game());

            gameNotifier.notifierAll(output.game(), overDto);
        }
    }

    @Override
    public Class<RevealActionDTO> getType() {
        return RevealActionDTO.class;
    }
}
