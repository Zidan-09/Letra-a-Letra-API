package com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.history.GameHistory;
import com.letraaletra.api.features.game.domain.history.MatchHistory;
import com.letraaletra.api.features.game.domain.history.SpectatorHistory;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.GameJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchPlayersJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchSpectatorsJpaEntity;
import com.letraaletra.api.features.player.domain.PlayerHistory;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameMapper {

    public static GameJpaEntity toEntity(Game game) {
        GameJpaEntity entity = new GameJpaEntity();

        entity.setId(game.getId());
        entity.setHostId(game.getHostId());
        entity.setRoomName(game.getRoomName());
        entity.setCreatorId(game.getCreatedById());
        entity.setRoomCode(game.getCode());
        entity.setGameType(game.getGameType());
        entity.setStatus(game.getGameStatus());
        if (game.getRoomSettings() != null) {
            entity.setAllowSpectators(game.getRoomSettings().roomAllowSpectators());
            entity.setPrivateGame(game.getRoomSettings().isPrivateGame());
        }

        return entity;
    }

    public static GameHistory toDomain(
            GameJpaEntity entity,
            List<MatchJpaEntity> matches,
            Map<UUID, List<MatchPlayersJpaEntity>> playersByMatch
    ) {
        return toDomain(entity, matches, playersByMatch, Map.of());
    }

    public static GameHistory toDomain(
            GameJpaEntity entity,
            List<MatchJpaEntity> matches,
            Map<UUID, List<MatchPlayersJpaEntity>> playersByMatch,
            Map<UUID, List<MatchSpectatorsJpaEntity>> spectatorsByMatch
    ) {

        return new GameHistory(
                entity.getId(),
                entity.getRoomName(),
                entity.getGameType(),
                entity.getStatus(),
                convert(matches, playersByMatch, spectatorsByMatch)
        );
    }

    private static List<MatchHistory> convert(
            List<MatchJpaEntity> matches,
            Map<UUID, List<MatchPlayersJpaEntity>> playersByMatch
    ) {
        return convert(matches, playersByMatch, Map.of());
    }

    private static List<MatchHistory> convert(
            List<MatchJpaEntity> matches,
            Map<UUID, List<MatchPlayersJpaEntity>> playersByMatch,
            Map<UUID, List<MatchSpectatorsJpaEntity>> spectatorsByMatch
    ) {

        List<MatchHistory> result = new ArrayList<>();

        for (MatchJpaEntity match : matches) {

            List<PlayerHistory> players =
                    convertPlayers(
                            playersByMatch.getOrDefault(
                                    match.getId(),
                                    List.of()
                            )
                    );

            List<SpectatorHistory> spectators =
                    convertSpectators(
                            spectatorsByMatch.getOrDefault(
                                    match.getId(),
                                    List.of()
                            )
                    );

            result.add(
                    new MatchHistory(
                            players,
                            spectators,
                            match.getEndedAt() != null ? match.getEndedAt().toInstant(ZoneOffset.UTC) : Instant.now()
                    )
            );
        }

        return result;
    }

    private static List<PlayerHistory> convertPlayers(
            List<MatchPlayersJpaEntity> entities
    ) {

        List<PlayerHistory> players = new ArrayList<>();

        for (MatchPlayersJpaEntity entity : entities) {

            players.add(
                    new PlayerHistory(
                            entity.getMatchPlayerId().getUserId(),
                            entity.getNickname(),
                            entity.getScore(),
                            entity.isWinner()
                    )
            );
        }

        return players;
    }

    private static List<SpectatorHistory> convertSpectators(
            List<MatchSpectatorsJpaEntity> entities
    ) {

        List<SpectatorHistory> spectators = new ArrayList<>();

        for (MatchSpectatorsJpaEntity entity : entities) {

            spectators.add(
                    new SpectatorHistory(
                            entity.getMatchSpectatorId().getUserId(),
                            entity.getNickname()
                    )
            );
        }

        return spectators;
    }
}