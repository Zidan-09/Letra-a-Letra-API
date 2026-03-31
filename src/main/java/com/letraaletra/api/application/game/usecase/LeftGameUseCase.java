package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.infra.service.GlobalTokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.game.exceptions.UserNotInGameException;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.infra.websocket.BroadcastService;
import com.letraaletra.api.presentation.dto.mappers.GameDTOMapper;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameUpdatedWsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeftGameUseCase {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GlobalTokenService globalTokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameDTOMapper gameDTOMapper;

    @Autowired
    private MapParticipantsService mapParticipantsService;

    @Autowired
    private BroadcastService broadCast;

    public void execute(String tokenGameId, String sessionId) {
        String gameId = globalTokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId);

        validateGame(game);

        Participant participant = game.getParticipant(sessionId);

        validateParticipant(participant);

        User user = userRepository.find(participant.getUserId());

        validateUser(user);

        user.leaveGame();
        game.remove(participant.getUserId());

        if (game.getParticipants().isEmpty()) {
            gameRepository.removeByCode(game.getCode());
        } else {
            gameRepository.save(game);
        }

        userRepository.save(user);

        List<ParticipantDTO> participantDTOS = mapParticipantsService.execute(game);

        GameUpdatedWsResponse data = buildResponse(game, tokenGameId, participantDTOS);

        broadCast.send(gameId, data);
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void validateParticipant(Participant participant) {
        if (participant == null) {
            throw new UserNotInGameException();
        }
    }

    private GameUpdatedWsResponse buildResponse(Game game, String tokenGameId, List<ParticipantDTO> participantDTOS) {
        return new GameUpdatedWsResponse(
                gameDTOMapper.toDTO(game, tokenGameId, participantDTOS)
        );
    }
}
