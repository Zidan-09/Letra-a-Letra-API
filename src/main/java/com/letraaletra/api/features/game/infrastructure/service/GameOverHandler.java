package com.letraaletra.api.features.game.infrastructure.service;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.application.port.GameOverService;
import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;
import com.letraaletra.api.features.ranking.application.port.RankingPointsService;
import com.letraaletra.api.features.user.application.port.UserStatsService;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.GameOver;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameOverHandler implements GameOverService {
    private static final String SOURCE_DETAIL = "MATCH_END";

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ActorManager<Game> actorManager;
    private final RoomTimeoutManager roomTimeoutManager;
    private final UserStatsService userStatsService;
    private final RankingPointsService rankingPointsService;
    private final BusinessAuditRecorder auditRecorder;

    @Override
    public HandledGameOver handle(Game game, GameOver result) {
        List<User> userList = userRepository.findUsersById(List.of(
                result.winner().getUserId(),
                result.loser().getUserId()
        ));

        User userWinner = userList.stream()
                .filter(u -> u.getUserId().equals(result.winner().getUserId()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);

        User userLoser = userList.stream()
                .filter(u -> u.getUserId().equals(result.loser().getUserId()))
                .findFirst()
                .orElseThrow(UserNotFoundException::new);

        userStatsService.update(userWinner, true);
        userStatsService.update(userLoser, false);

        HandledGameOver handled = HandledGameOver.withoutRanking();

        if (game.getGameType().equals(GameType.RANKING)) {
            UpdateRankingPoints winnerPoints = rankingPointsService.handle(
                    userWinner,
                    result.winner().getScore(),
                    result.loser().getScore()
            );

            UpdateRankingPoints loserPoints = rankingPointsService.handle(
                    userLoser,
                    result.loser().getScore(),
                    result.winner().getScore()
            );

            handled = HandledGameOver.withRanking(winnerPoints, loserPoints);
        }

        if (game.getGameType().equals(GameType.CUSTOM)) {
            game.setGameStatus(GameStatus.WAITING);
            roomTimeoutManager.start(game);
        } else {
            game.setGameStatus(GameStatus.CLOSED);
        }

        if (game.getGameStatus().equals(GameStatus.CLOSED)) {
            actorManager.remove(game.getId());
            releaseParticipants(userList);
        }

        userRepository.saveAll(List.of(userWinner, userLoser));
        gameRepository.save(game);

        recordMatchEnded(game, result);

        return handled;
    }

    private void releaseParticipants(List<User> userList) {
        userList.forEach(User::leaveGame);

        userRepository.saveAll(userList);
    }

    private void recordMatchEnded(Game game, GameOver result) {
        UUID matchId = game.getGameState() != null ? game.getGameState().getMatchId() : null;

        if (matchId == null) {
            return;
        }

        auditRecorder.record(AuditEvent.builder()
                .category(AuditCategory.GAME)
                .eventType(AuditEventType.MATCH_ENDED)
                .actor(AuditActor.system())
                .resourceType(AuditResourceType.MATCH)
                .resourceId(matchId.toString())
                .correlationId(game.getId().toString())
                .sourceType(AuditSourceType.SYSTEM)
                .sourceDetail(SOURCE_DETAIL)
                .metadata(Map.of(
                        "gameId", game.getId().toString(),
                        "gameType", game.getGameType().name(),
                        "winnerUserId", result.winner().getUserId().toString(),
                        "winnerNickname", result.winner().getNickname(),
                        "winnerScore", result.winner().getScore(),
                        "loserUserId", result.loser().getUserId().toString(),
                        "loserNickname", result.loser().getNickname(),
                        "loserScore", result.loser().getScore()
                ))
                .build());
    }
}
