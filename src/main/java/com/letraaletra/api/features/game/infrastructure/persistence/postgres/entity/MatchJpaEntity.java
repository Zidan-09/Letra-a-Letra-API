package com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.game.domain.state.GameMode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"matches\"")
public class MatchJpaEntity {
    @Id
    @Column(name = "match_id")
    private UUID id;

    @Column(name = "game_id")
    private UUID gameId;

    @Enumerated(EnumType.STRING)
    @Column(name = "game_mode")
    private GameMode gameMode;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
}
