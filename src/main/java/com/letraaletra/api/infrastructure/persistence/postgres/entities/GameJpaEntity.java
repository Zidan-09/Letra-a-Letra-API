package com.letraaletra.api.infrastructure.persistence.postgres.entities;

import com.letraaletra.api.domain.game.GameStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
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

    @Column(name = "created_by_id")
    private UUID creatorId;

    @Column(name = "room_code", unique = true, nullable = false)
    private String roomCode;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GameStatus status;
}
