package com.letraaletra.api.service;

import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.dto.response.game.BoardDTO;
import com.letraaletra.api.dto.response.game.GameStateDTO;
import com.letraaletra.api.dto.response.player.PlayerDTO;
import com.letraaletra.api.infra.repository.UserRepository;
import com.letraaletra.api.service.mappers.BoardDTOMapper;
import com.letraaletra.api.service.mappers.GameStateDTOMapper;
import com.letraaletra.api.service.mappers.PlayerDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameStateAssembler {
    @Autowired
    private GameStateDTOMapper gameStateDTOMapper;

    @Autowired
    private BoardDTOMapper boardDTOMapper;

    @Autowired
    private PlayerDTOMapper playerDTOMapper;

    @Autowired
    private UserRepository userRepository;

    public GameStateDTO get(GameState gameState) {
        List<PlayerDTO> playerDTOS = gameState.getPlayers().values()
                .stream().map(p -> playerDTOMapper.toDTO(p, userRepository.find(p.getUserId())))
                .toList();

        BoardDTO[][] boardDTO = boardDTOMapper.toDTO(gameState.getBoard());

        return gameStateDTOMapper.toDTO(playerDTOS, boardDTO, gameState.currentPlayerTurn());
    }
}
