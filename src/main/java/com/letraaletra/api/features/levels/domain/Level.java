package com.letraaletra.api.features.levels.domain;

import java.util.List;
import java.util.UUID;

public class Level {
    private final UUID levelId;
    private int level;
    private List<LevelReward> rewards;

    public Level(
            UUID levelId,
            int level,
            List<LevelReward> rewards
    ) {
        this.levelId = levelId;
        this.level = level;
        this.rewards = rewards;
    }

    public static Level create(
            int level,
            List<LevelReward> rewards
    ) {
        return new Level(
                UUID.randomUUID(),
                level,
                rewards
        );
    }

    public UUID getLevelId() {
        return levelId;
    }

    public int getLevel() {
        return level;
    }

    public List<LevelReward> getRewards() {
        return rewards;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setRewards(List<LevelReward> rewards) {
        this.rewards = rewards;
    }
}
