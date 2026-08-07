package com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection;

import com.letraaletra.api.features.user.domain.BanType;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserProjection {
    UUID getUserId();

    String getUsername();
    String getEmail();
    String getPasswordHash();
    String getGoogleId();

    UUID getCurrentGameId();
    boolean isCanChangeNickname();

    BanType getBanType();
    String getBanReason();
    LocalDateTime getBanExpiresAt();

    int getTotalMatches();
    int getTotalWins();
    int getWinStreak();
    int getLevel();
    int getExperience();
    int getRankingPoints();

    long getSoftCoins();
    long getHardGems();

    LocalDateTime getCreatedAt();
}