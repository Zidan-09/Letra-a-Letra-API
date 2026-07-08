package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.offers.domain.RewardType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"level_reward\"")
public class LevelRewardJpaEntity {
    @Id
    @Column(name = "level_reward_id")
    private UUID levelRewardId;

    @Column(name = "level_id")
    private UUID levelId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type")
    private RewardType rewardType;

    @Column(name = "reward_reference")
    private UUID rewardReference;

    @Column(name = "quantity")
    private Integer quantity;
}
