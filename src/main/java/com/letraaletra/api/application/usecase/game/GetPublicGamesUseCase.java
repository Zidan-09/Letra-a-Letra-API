package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.infrastructure.security.JsonWebTokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.presentation.mappers.game.GameDTOMapper;
import com.letraaletra.api.presentation.dto.response.game.GameDTO;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetPublicGamesUseCase {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private JsonWebTokenService jsonWebTokenService;

    @Autowired
    private GameDTOMapper gameDTOMapper;

    @Autowired
    private MapParticipantsService mapParticipants;

    public List<GameDTO> execute() {
        return gameRepository.get().stream()
                .filter(game -> !game.getRoomSettings().isPrivateGame())
                .map(this::toPublicGameDTO)
                .toList();
    }

    private GameDTO toPublicGameDTO(Game game) {
        String token = jsonWebTokenService.generateToken(game.getId());
        List<ParticipantDTO> participants = mapParticipants.execute(game);
        return gameDTOMapper.toDTO(game, token, participants);
    }
}
