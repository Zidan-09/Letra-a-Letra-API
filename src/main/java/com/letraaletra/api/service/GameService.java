package com.letraaletra.api.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.GameState;
import com.letraaletra.api.domain.game.GameSettings;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.participant.ParticipantFactory;
import com.letraaletra.api.domain.participant.ParticipantRole;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.dto.response.game.GameDTO;
import com.letraaletra.api.dto.response.participant.ParticipantDTO;
import com.letraaletra.api.dto.response.websocket.GameStartedWsResponse;
import com.letraaletra.api.dto.response.websocket.GameUpdatedWsResponse;
import com.letraaletra.api.exception.exceptions.GameNotFoundException;
import com.letraaletra.api.exception.exceptions.OnlyHostCanStartException;
import com.letraaletra.api.exception.exceptions.SessionNotFoundException;
import com.letraaletra.api.exception.exceptions.UserNotFoundException;
import com.letraaletra.api.infra.repository.GameRepository;
import com.letraaletra.api.infra.repository.SessionRepository;
import com.letraaletra.api.infra.repository.UserRepository;
import com.letraaletra.api.service.mappers.GameDTOMapper;
import com.letraaletra.api.service.mappers.ParticipantDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private GameDTOMapper gameDTOMapper;

    @Autowired
    private GameStateService gameStateService;

    @Autowired
    private BroadCastService broadCastService;

    @Autowired
    private GameStateAssembler gameStateAssembler;

    @Autowired
    private ParticipantDTOMapper participantDTOMapper;

    public void createGame(String name, GameSettings gameSettings, String userId, String sessionId) {
        String gameId = UUID.randomUUID().toString();

        User user = userRepository.find(userId);

        if (user == null) {
            throw new UserNotFoundException();
        }

        Participant host = ParticipantFactory.fromUser(user, sessionId, ParticipantRole.PLAYER);

        Game game = new Game(gameId, name, gameSettings, host);

        gameRepository.save(game);

        String tokenGameId = tokenService.generateToken(gameId);

        List<ParticipantDTO> participantDTOS = mapParticipants(game);

        GameUpdatedWsResponse data = new GameUpdatedWsResponse(
                gameDTOMapper.toDTO(game, tokenGameId, participantDTOS)
        );

        broadCastService.broadCast(gameId, data);
    }

    public void joinGame(String tokenGameId, String sessionId) {
        String gameId = tokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId);

        WebSocketSession session = sessionRepository.find(sessionId);

        if (session == null) {
            throw new SessionNotFoundException();
        }

        String tokenUserId = (String) session.getAttributes().get("token");

        if (tokenUserId == null) {
            throw new UserNotFoundException();
        }

        String userId = tokenService.getTokenContent(tokenUserId);

        User user = userRepository.find(userId);

        if (user == null) {
            throw new UserNotFoundException();
        }

        ParticipantRole role = game.nextParticipantRole();

        Participant participant = ParticipantFactory.fromUser(user, sessionId, role);

        game.join(participant);

        List<ParticipantDTO> participantDTOS = mapParticipants(game);

        GameUpdatedWsResponse data = new GameUpdatedWsResponse(
                gameDTOMapper.toDTO(game, tokenGameId, participantDTOS)
        );

        broadCastService.broadCast(gameId, data);
    }

    public void startGame(String tokenGameId, String sessionId) {
        String gameId = tokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId);

        String hostId = game.getGameSettings().getHostId();
        Participant participant = game.findBySession(sessionId);

        if (participant == null) {
            throw new UserNotFoundException();
        }

        if (!participant.getUserId().equals(hostId)) {
            throw new OnlyHostCanStartException();
        }

        GameState gameState = gameStateService.generateGameState(game);

        game.updateGameState(gameState);

        GameStartedWsResponse data = new GameStartedWsResponse(
                gameStateAssembler.get(gameState)
        );

        broadCastService.broadCast(gameId, data);
    }

    public Game findById(String gameId) {
        Game game = gameRepository.find(gameId);

        if (game == null) {
            throw new GameNotFoundException();
        }

        return game;
    }

    public List<GameDTO> getGames() {
        return gameRepository.get().stream()
                .map(game -> {
                    String token = tokenService.generateToken(game.getId());

                    List<ParticipantDTO> participants = mapParticipants(game);

                    return gameDTOMapper.toDTO(game, token, participants);
                })
                .toList();
    }

    private List<ParticipantDTO> mapParticipants(Game game) {
        return game.getParticipants().stream()
                .map(participantDTOMapper::toDTO)
                .toList();
    }
}
