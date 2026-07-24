package com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameHistory;
import com.letraaletra.api.features.game.domain.state.MatchHistory;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.GameJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchPlayersJpaEntity;
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
        entity.setCreatorId(game.getCreatedById());
        entity.setRoomCode(game.getCode());
        entity.setGameType(game.getGameType());
        entity.setStatus(game.getGameStatus());

        return entity;
    }

    public static GameHistory toDomain(
            GameJpaEntity entity,
            List<MatchJpaEntity> matches,
            Map<UUID, List<MatchPlayersJpaEntity>> playersByMatch
    ) {

        return new GameHistory(
                entity.getId(),
                entity.getRoomName(),
                entity.getGameType(),
                entity.getStatus(),
                convert(matches, playersByMatch)
        );
    }

    private static List<MatchHistory> convert(
            List<MatchJpaEntity> matches,
            Map<UUID, List<MatchPlayersJpaEntity>> playersByMatch
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

            result.add(
                    new MatchHistory(
                            players,
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
}