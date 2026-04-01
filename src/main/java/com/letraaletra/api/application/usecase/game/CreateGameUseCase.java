package com.letraaletra.api.application.usecase.game;

import com.letraaletra.api.application.game.service.GenerateRoomCode;
import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.application.game.service.TimeoutManager;
import com.letraaletra.api.domain.broadcast.BroadCastService;
import com.letraaletra.api.domain.security.TokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.game.RoomSettings;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.participant.ParticipantFactory;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import com.letraaletra.api.presentation.mappers.game.GameDTOMapper;
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
    private TimeoutManager timeoutManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private GenerateRoomCode generateRoomCode;

    @Autowired
    private GameDTOMapper gameDTOMapper;

    @Autowired
    private BroadCastService broadCast;

    @Autowired
    private MapParticipantsService mapParticipantsService;

    public void execute(String name, RoomSettings roomSettings, String sessionId, String userId) {
        String gameId = UUID.randomUUID().toString();

        User user = userRepository.find(userId);

        validateUser(user);

        Participant host = ParticipantFactory.fromUser(user, sessionId, ParticipantRole.PLAYER);

        String code = getCode();

        Game game = new Game(gameId, code, name, roomSettings, host);

        user.enterGame(gameId);

        userRepository.save(user);
        gameRepository.save(game);

        String tokenGameId = tokenService.generateToken(gameId);

        List<ParticipantDTO> participantDTOS = mapParticipantsService.execute(game);

        GameUpdatedWsResponse response = buildResponse(game, tokenGameId, participantDTOS);

        timeoutManager.start(gameId);

        broadCast.send(gameId, response);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new UserNotFoundException();
        }
    }

    private String getCode() {
        String code;

        do {
            code = generateRoomCode.execute();

        } while (gameRepository.existsByCode(code));

        return code;
    }

    private GameUpdatedWsResponse buildResponse(Game game, String tokenGameId, List<ParticipantDTO> participantDTOS) {
        return new GameUpdatedWsResponse(
                gameDTOMapper.toDTO(game, tokenGameId, participantDTOS)
        );
    }
}
