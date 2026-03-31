package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.infra.service.GlobalTokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.presentation.dto.mappers.GameDTOMapper;
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
    private GlobalTokenService globalTokenService;

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
        String token = globalTokenService.generateToken(game.getId());
        List<ParticipantDTO> participants = mapParticipants.execute(game);
        return gameDTOMapper.toDTO(game, token, participants);
    }
}
