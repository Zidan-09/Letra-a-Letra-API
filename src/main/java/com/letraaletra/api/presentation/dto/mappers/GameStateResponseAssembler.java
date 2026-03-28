package com.letraaletra.api.presentation.dto.mappers;

import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.board.Word;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.presentation.dto.response.game.BoardDTO;
import com.letraaletra.api.presentation.dto.response.game.GameStateDTO;
import com.letraaletra.api.presentation.dto.response.player.PlayerDTO;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class GameStateResponseAssembler {
    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    @Autowired
    private BoardDTOMapper boardDTOMapper;

    @Autowired
    private PlayerDTOMapper playerDTOMapper;

    public GameStateDTO get(GameState gameState, Map<String, User> users) {
        List<PlayerDTO> playerDTOS = mapPlayers(gameState, users);

        List<Word> words = Arrays.stream(gameState.getBoard().words()).toList();

        BoardDTO[][] boardDTO = boardDTOMapper.toDTO(gameState.getBoard());

        return gameStateDTOMapper.toDTO(playerDTOS, boardDTO, words, gameState.currentPlayerTurn());
    }

    private List<PlayerDTO> mapPlayers(GameState state, Map<String, User> users) {
        return state.getPlayers().values()
                .stream()
                .map(p -> {
                    User user = users.get(p.getUserId());
                    if (user == null) {
                        throw new UserNotFoundException();
                    }
                    return playerDTOMapper.toDTO(p, user);
                })
                .toList();
    }
}
