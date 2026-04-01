package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.domain.game.exceptions.UserBannedException;
import com.letraaletra.api.infrastructure.security.JsonWebTokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.exceptions.GameNotFoundException;
import com.letraaletra.api.domain.game.exceptions.RoomFullException;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.participant.ParticipantFactory;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.infrastructure.websocket.BroadcastService;
import com.letraaletra.api.presentation.mappers.game.GameDTOMapper;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameUpdatedWsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JoinGameUseCase {
    @Autowired
    private JsonWebTokenService jsonWebTokenService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MapParticipantsService mapParticipantsService;

    @Autowired
    private GameDTOMapper gameDTOMapper;

    @Autowired
    private BroadcastService broadCast;

    public void execute(String tokenGameId, String sessionId, String userId) {
        String gameId = jsonWebTokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId);

        validateGame(game);

        User user = userRepository.find(userId);

        validateUser(user);
        checkIfBlackListed(game, userId);

        ParticipantRole role = game.nextParticipantRole();

        Participant participant = ParticipantFactory.fromUser(user, sessionId, role);

        try {
            user.enterGame(gameId);
            game.join(participant);
        } catch (Exception e) {
            user.leaveGame();
            throw e;
        }

        userRepository.save(user);
        gameRepository.save(game);

        List<ParticipantDTO> participantDTOS = mapParticipantsService.execute(game);

        GameUpdatedWsResponse data = buildResponse(game, tokenGameId, participantDTOS);

        broadCast.send(gameId, data);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private void checkIfBlackListed(Game game, String userId) {
        if (game.isBlackListed(userId)) {
            throw new UserBannedException();
        }
    }

    private void validateGame(Game game) {
        if (game == null) {
            throw new GameNotFoundException();
        }

        if (game.getParticipants().size() == 7) {
            throw new RoomFullException();
        }
    }

    private GameUpdatedWsResponse buildResponse(Game game, String tokenGameId, List<ParticipantDTO> participantDTOS) {
        return new GameUpdatedWsResponse(
                gameDTOMapper.toDTO(game, tokenGameId, participantDTOS)
        );
    }
}
