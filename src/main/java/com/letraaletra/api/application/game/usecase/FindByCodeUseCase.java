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
import java.util.Optional;

@Service
public class FindByCodeUseCase {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MapParticipantsService mapParticipantsService;

    @Autowired
    private GlobalTokenService globalTokenService;

    @Autowired
    private GameDTOMapper gameDTOMapper;

    public Optional<GameDTO> execute(String roomCode) {
        Game game = gameRepository.findByCode(roomCode);

        if (game == null) {
            return Optional.empty();
        }

        String tokenGameId = globalTokenService.generateToken(game.getId());

        List<ParticipantDTO> participantDTOS = mapParticipantsService.execute(game);

        GameDTO gameDTO = gameDTOMapper.toDTO(game, tokenGameId, participantDTOS);

        return Optional.of(gameDTO);
    }
}
