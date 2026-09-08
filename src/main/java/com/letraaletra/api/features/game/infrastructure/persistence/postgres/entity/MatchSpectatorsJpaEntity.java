package com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "\"match_spectators\"")
public class MatchSpectatorsJpaEntity {
    @EmbeddedId
    private MatchSpectatorId matchSpectatorId;

    @Column(name = "nickname")
    private String nickname;
}
