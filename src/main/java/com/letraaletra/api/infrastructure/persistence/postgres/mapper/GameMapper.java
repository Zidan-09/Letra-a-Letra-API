package com.letraaletra.api.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.game.participant.factory.ParticipantFactory;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.infrastructure.persistence.postgres.entities.GameJpaEntity;

import java.util.UUID;

public class GameMapper {
    public static Game toDomain(GameJpaEntity entity) {
        return new Game(
                entity.getId().toString(),
                entity.getRoomCode(),
                entity.getRoomCode(),
                null,
                ParticipantFactory.fromUser(new User("id", "nickname", "avatar", "email", "senha", "id2"), "session"),
                entity.getGameType()
        );
    }

    public static GameJpaEntity toEntity(Game game) {
        GameJpaEntity entity = new GameJpaEntity();

        entity.setId(UUID.fromString(game.getId()));
        entity.setRoomCode(game.getCode());
        entity.setHostId(UUID.fromString(game.getHostId()));
        entity.setStatus(game.getGameStatus());

        return entity;
    }
}
