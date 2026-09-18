package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.ban.BanInfo;
import com.letraaletra.api.features.user.domain.ban.BanType;
import com.letraaletra.api.features.user.domain.effect.ActiveEffects;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.domain.wallet.Wallet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public final class UserProcedureMapper {

    private UserProcedureMapper() {}

    public static User toDomain(ResultSet rs) throws SQLException {
        UUID userId = (UUID) rs.getObject("user_id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String passwordHash = rs.getString("password_hash");
        String googleId = rs.getString("google_id");
        UUID tokenVersion = (UUID) rs.getObject("token_version");
        UUID currentGameId = (UUID) rs.getObject("current_game_id");
        boolean canChangeNickname = rs.getBoolean("can_change_nickname");
        String banTypeStr = rs.getString("ban_type");
        String banReason = rs.getString("ban_reason");
        java.sql.Timestamp banExpiresTs = rs.getTimestamp("ban_expires_at");
        int totalMatches = rs.getInt("total_matches");
        int totalWins = rs.getInt("total_wins");
        int winStreak = rs.getInt("win_streak");
        int level = rs.getInt("level");
        int experience = rs.getInt("experience");
        int rankingPoints = rs.getInt("ranking_points");
        long softCoins = rs.getLong("soft_coins");
        long hardGems = rs.getLong("hard_gems");
        java.sql.Timestamp createdTs = rs.getTimestamp("created_at");

        BanInfo banInfo;
        if (banTypeStr == null) {
            banInfo = BanInfo.create();
        } else {
            BanType bt = BanType.valueOf(banTypeStr);
            LocalDateTime exp = banExpiresTs != null ? banExpiresTs.toLocalDateTime() : null;
            banInfo = BanInfo.restore(bt, banReason, exp);
        }

        LocalDateTime createdAt = createdTs != null ? createdTs.toLocalDateTime() : LocalDateTime.now();

        return User.restore(
                userId,
                username,
                email,
                passwordHash,
                tokenVersion,
                googleId,
                currentGameId,
                canChangeNickname,
                banInfo,
                UserStats.restore(totalMatches, totalWins, winStreak, level, experience, rankingPoints),
                Wallet.restore(softCoins, hardGems),
                ActiveEffects.create(),
                createdAt
        );
    }
}
