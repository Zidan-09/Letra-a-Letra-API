package com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.GameType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "\"game\"")
@Getter
@Setter
public class GameJpaEntity {
    @Id
    @Column(name = "game_id", nullable = false)
    private UUID id;

    @Column(name = "host_id")
    private UUID hostId;

    @Column(name = "room_name")
    private String roomName;

    @Column(name = "created_by_id")
    private UUID creatorId;

    @Column(name = "room_code", nullable = false)
    private String roomCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_type", nullable = false)
    private GameType gameType;

    @Column(name = "allow_spectators")
    private boolean allowSpectators;

    @Column(name = "private_game")
    private boolean privateGame;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameStatus status;
}
