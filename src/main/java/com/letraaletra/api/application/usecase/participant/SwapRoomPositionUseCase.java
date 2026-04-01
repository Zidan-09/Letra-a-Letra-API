package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.infrastructure.security.JsonWebTokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.GameStatus;
import com.letraaletra.api.domain.game.exceptions.GameIsRunningException;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.game.exceptions.UserNotInGameException;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.infrastructure.websocket.BroadcastService;
import com.letraaletra.api.presentation.mappers.game.GameDTOMapper;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameUpdatedWsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SwapRoomPositionUseCase {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private JsonWebTokenService jsonWebTokenService;

    @Autowired
    private GameDTOMapper gameDTOMapper;

    @Autowired
    private MapParticipantsService mapParticipantsService;

    @Autowired
    private BroadcastService broadcast;

    public void execute(String tokenGameId, int position, String userId) {
        String gameId = jsonWebTokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId);

        validateGame(game);

        Participant participant = game.getParticipantByUserId(userId);

        validateParticipant(participant);

        game.changePosition(userId, position);

        gameRepository.save(game);

        List<ParticipantDTO> participants = mapParticipantsService.execute(game);

        GameUpdatedWsResponse response = buildResponse(game, tokenGameId, participants);

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

    private GameUpdatedWsResponse buildResponse(Game game, String tokenGameId, List<ParticipantDTO> participants) {
        return new GameUpdatedWsResponse(
                gameDTOMapper.toDTO(game, tokenGameId, participants)
        );
    }
}
