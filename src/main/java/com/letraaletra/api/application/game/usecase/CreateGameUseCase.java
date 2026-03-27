package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.application.user.service.TokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.GameSettings;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.participant.ParticipantFactory;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.domain.repository.GameRepository;
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
import java.util.UUID;

@Service
public class CreateGameUseCase {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private GameDTOMapper gameDTOMapper;

    @Autowired
    private BroadcastService broadCast;

    @Autowired
    private MapParticipantsService mapParticipantsService;

    public void execute(String name, GameSettings gameSettings, String sessionId, String userId) {
        String gameId = UUID.randomUUID().toString();

        User user = userRepository.find(userId);

        validateUser(user);

        Participant host = ParticipantFactory.fromUser(user, sessionId, ParticipantRole.PLAYER);

        Game game = new Game(gameId, name, gameSettings, host);

        gameRepository.save(game);

        String tokenGameId = tokenService.generateToken(gameId);

        List<ParticipantDTO> participantDTOS = mapParticipantsService.execute(game);

        GameUpdatedWsResponse response = buildResponse(game, tokenGameId, participantDTOS);

        broadCast.send(gameId, response);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private GameUpdatedWsResponse buildResponse(Game game, String tokenGameId, List<ParticipantDTO> participantDTOS) {
        return new GameUpdatedWsResponse(
                gameDTOMapper.toDTO(game, tokenGameId, participantDTOS)
        );
    }
}
