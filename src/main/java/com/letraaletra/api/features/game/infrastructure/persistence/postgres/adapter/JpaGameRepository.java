package com.letraaletra.api.features.game.infrastructure.persistence.postgres.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GamesPage;
import com.letraaletra.api.features.game.domain.history.GameHistory;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.GameJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchPlayersJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchSpectatorsJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataGameRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataMatchPlayerRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataMatchRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataMatchSpectatorRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper.GameMapper;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper.MatchMapper;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper.MatchPlayerMapper;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper.MatchSpectatorMapper;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.infrastructure.persistence.ProcedureExceptionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaGameRepository implements GameRepository {
    private final SpringDataGameRepository repository;
    private final SpringDataMatchRepository matchRepository;
    private final SpringDataMatchPlayerRepository matchPlayerRepository;
    private final SpringDataMatchSpectatorRepository matchSpectatorRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public JpaGameRepository(
            SpringDataGameRepository repository,
            SpringDataMatchRepository matchRepository,
            SpringDataMatchPlayerRepository matchPlayerRepository,
            SpringDataMatchSpectatorRepository matchSpectatorRepository,
            @Autowired(required = false) JdbcTemplate jdbcTemplate
    ) {
        this.repository = repository;
        this.matchRepository = matchRepository;
        this.matchPlayerRepository = matchPlayerRepository;
        this.matchSpectatorRepository = matchSpectatorRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Legacy constructor for tests without JdbcTemplate / without spectator repo
    public JpaGameRepository(
            SpringDataGameRepository repository,
            SpringDataMatchRepository matchRepository,
            SpringDataMatchPlayerRepository matchPlayerRepository
    ) {
        this(repository, matchRepository, matchPlayerRepository, null, null);
    }

    public JpaGameRepository(
            SpringDataGameRepository repository,
            SpringDataMatchRepository matchRepository,
            SpringDataMatchPlayerRepository matchPlayerRepository,
            SpringDataMatchSpectatorRepository matchSpectatorRepository
    ) {
        this(repository, matchRepository, matchPlayerRepository, matchSpectatorRepository, null);
    }

    @Override
    public void save(Game game) {
        if (jdbcTemplate == null) {
            legacySave(game);
            return;
        }
        try {
            UUID matchId = null;
            String gameMode = null;
            Timestamp endedAt = null;
            String playersJson = null;
            String spectatorsJson = null;

            if (game.getGameState() != null) {
                matchId = game.getGameState().getMatchId();
                gameMode = game.getGameState().getBoard() != null && game.getGameState().getBoard().gameMode() != null ? game.getGameState().getBoard().gameMode().name() : null;
                if (!game.getGameStatus().equals(GameStatus.RUNNING)) {
                    endedAt = Timestamp.valueOf(LocalDateTime.now());
                }
                List<Map<String, Object>> players = new ArrayList<>();
                for (Player player : game.getGameState().getPlayers().values()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("user_id", player.getUserId().toString());
                    m.put("nickname", player.getNickname());
                    m.put("score", player.getScore());
                    m.put("is_winner", player.getScore() == 3);
                    players.add(m);
                }
                try {
                    playersJson = objectMapper.writeValueAsString(players);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                List<Map<String, Object>> spectators = new ArrayList<>();
                for (Participant spectator : game.getParticipants().getParticipants().stream().filter(Participant::isSpectator).toList()) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("user_id", spectator.getUserId().toString());
                    m.put("nickname", spectator.getNickname());
                    spectators.add(m);
                }
                try {
                    spectatorsJson = objectMapper.writeValueAsString(spectators);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            jdbcTemplate.update(
                    "CALL sp_game_save(?, ?, ?, ?, ?, ?, ?, ?::uuid, ?::varchar, ?::timestamp, ?::jsonb, ?::jsonb)",
                    game.getId(),
                    game.getHostId(),
                    game.getRoomName(),
                    game.getCreatedById(),
                    game.getCode(),
                    game.getGameType() != null ? game.getGameType().name() : null,
                    game.getGameStatus() != null ? game.getGameStatus().name() : null,
                    matchId,
                    gameMode,
                    endedAt,
                    playersJson,
                    spectatorsJson
            );
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) {
                legacySave(game);
                return;
            }
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private boolean isProcedureMissing(DataAccessException ex) {
        Throwable cursor = ex;
        while (cursor != null) {
            if (cursor instanceof SQLException sqlEx) {
                String state = sqlEx.getSQLState();
                if ("42883".equals(state)) return true;
            }
            cursor = cursor.getCause();
        }
        if (ex instanceof org.springframework.jdbc.BadSqlGrammarException) return true;
        String msg = ex.getMessage();
        if (msg != null) {
            String lower = msg.toLowerCase();
            if (lower.contains("sp_") || lower.contains("h2") || lower.contains("syntax error") || lower.contains("function") || lower.contains("procedure")) return true;
        }
        Throwable cause = ex.getCause();
        while (cause != null) {
            if (cause instanceof SQLException sqlEx) {
                String state = sqlEx.getSQLState();
                if ("42883".equals(state)) return true;
            }
            String cmsg = cause.getMessage();
            if (cmsg != null) {
                String lower = cmsg.toLowerCase();
                if (lower.contains("sp_") || lower.contains("h2") || lower.contains("syntax error")) return true;
            }
            cause = cause.getCause();
        }
        return false;
    }

    private void legacySave(Game game) {
        repository.save(GameMapper.toEntity(game));
        if (game.getGameState() != null) {
            LocalDateTime endedAt = !game.getGameStatus().equals(GameStatus.RUNNING) ? LocalDateTime.now() : null;
            matchRepository.save(MatchMapper.toEntity(game.getGameState(), game.getId(), endedAt));
            for (Player player : game.getGameState().getPlayers().values()) {
                matchPlayerRepository.save(MatchPlayerMapper.toEntity(player, game.getGameState().getMatchId()));
            }
            if (matchSpectatorRepository != null) {
                for (Participant spectator : game.getParticipants().getParticipants().stream().filter(Participant::isSpectator).toList()) {
                    matchSpectatorRepository.save(MatchSpectatorMapper.toEntity(spectator, game.getGameState().getMatchId()));
                }
            }
        }
    }

    @Override
    public Page<GameHistory> get(GamesPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        Page<GameJpaEntity> games = repository.findAll(pageable);
        if (games.isEmpty()) {
            return games.map(game -> GameMapper.toDomain(
                    game,
                    List.of(),
                    Map.of(),
                    Map.of()
            ));
        }

        List<UUID> gameIds = games.stream()
                .map(GameJpaEntity::getId)
                .toList();

        Map<UUID, List<MatchJpaEntity>> matchesByGame =
                matchRepository.findByGameIdIn(gameIds).stream()
                        .collect(Collectors.groupingBy(MatchJpaEntity::getGameId));

        List<UUID> matchIds = matchesByGame.values().stream()
                .flatMap(List::stream)
                .map(MatchJpaEntity::getId)
                .toList();

        Map<UUID, List<MatchPlayersJpaEntity>> playersByMatch = Map.of();
        Map<UUID, List<MatchSpectatorsJpaEntity>> spectatorsByMatch = Map.of();

        if (!matchIds.isEmpty()) {
            playersByMatch = matchPlayerRepository.findByMatchPlayerIdMatchIdIn(matchIds)
                    .stream()
                    .collect(Collectors.groupingBy(
                            player -> player.getMatchPlayerId().getMatchId()
                    ));

            if (matchSpectatorRepository != null) {
                spectatorsByMatch = matchSpectatorRepository.findByMatchSpectatorIdMatchIdIn(matchIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                spectator -> spectator.getMatchSpectatorId().getMatchId()
                        ));
            }
        }

        final Map<UUID, List<MatchPlayersJpaEntity>> players = playersByMatch;
        final Map<UUID, List<MatchSpectatorsJpaEntity>> spectators = spectatorsByMatch;

        return games.map(game -> GameMapper.toDomain(
                game,
                matchesByGame.getOrDefault(game.getId(), List.of()),
                players,
                spectators
        ));
    }
}

