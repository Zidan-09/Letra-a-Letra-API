package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exceptions.GameIsRunningException;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.game.exceptions.OnlyHostCanKickException;
import com.letraaletra.api.domain.game.exceptions.UserNotInGameException;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.infra.websocket.BroadcastService;
import com.letraaletra.api.presentation.dto.mappers.GameDTOMapper;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameUpdatedWsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KickParticipantUseCase {
    @Autowired
    private TokenService tokenService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MapParticipantsService mapParticipantsService;

    @Autowired
    private GameDTOMapper gameDTOMapper;

    @Autowired
    private BroadcastService broadcast;

    public void execute(String tokenGameId, String participantId, String userId) {
        String gameId = tokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId);

        validateGame(game);
        validateUser(userId, game);

        Participant participant = game.getParticipantByUserId(participantId);

        validateParticipant(participant);

        game.remove(participantId);

        gameRepository.save(game);

        List<ParticipantDTO> participantDTOS = mapParticipantsService.execute(game);

        GameUpdatedWsResponse response = buildResponse(game, tokenGameId, participantDTOS);

        broadcast.send(gameId, response);
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        if (game.getGameStatus().equals(GameStatus.RUNNING)) {
            throw new GameIsRunningException();
        }
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotInGameException();
        }
    }

    private void validateUser(String userId, Game game) {
        if (!game.getHostId().equals(userId)) {
            throw new OnlyHostCanKickException();
        }
    }

    private GameUpdatedWsResponse buildResponse(Game game, String tokenGameId, List<ParticipantDTO> participantDTOS) {
        return new GameUpdatedWsResponse(
                gameDTOMapper.toDTO(game, tokenGameId, participantDTOS)
        );
    }
}
