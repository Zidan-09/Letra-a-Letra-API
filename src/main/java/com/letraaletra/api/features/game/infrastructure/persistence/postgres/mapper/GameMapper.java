package com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.GameJpaEntity;

import java.util.UUID;

public class GameMapper {
    public static GameJpaEntity toEntity(Game game) {
        GameJpaEntity entity = new GameJpaEntity();

        entity.setId(UUID.fromString(game.getId()));
        entity.setHostId(UUID.fromString(game.getHostId()));
        entity.setCreatorId(UUID.fromString(game.getCreatedById()));
        entity.setRoomCode(game.getCode());
        entity.setGameType(game.getGameType());
        entity.setStatus(game.getGameStatus());

        return entity;
    }
}
