package com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.ban.BanInfo;
import com.letraaletra.api.features.user.domain.ban.BanType;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.domain.wallet.Wallet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class UserProcedureMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

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
        String inventoryJson = rs.getString("inventory");

        BanInfo banInfo;
        if (banTypeStr == null) {
            banInfo = BanInfo.create();
        } else {
            BanType bt = BanType.valueOf(banTypeStr);
            LocalDateTime exp = banExpiresTs != null ? banExpiresTs.toLocalDateTime() : null;
            banInfo = BanInfo.restore(bt, banReason, exp);
        }

        List<InventoryItem> items = parseInventory(inventoryJson);
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
                Inventory.restore(items),
                Wallet.restore(softCoins, hardGems),
                createdAt
        );
    }

    private static List<InventoryItem> parseInventory(String json) {
        if (json == null || json.isBlank() || json.equals("[]") || json.equals("null")) {
            return List.of();
        }
        try {
            JsonNode array = MAPPER.readTree(json);
            if (!array.isArray()) return List.of();
            List<InventoryItem> list = new ArrayList<>();
            for (JsonNode node : array) {
                UUID cosmeticId = UUID.fromString(node.get("cosmetic_id").asText());
                String name = node.has("name") && !node.get("name").isNull() ? node.get("name").asText() : "";
                String typeStr = node.has("type") && !node.get("type").isNull() ? node.get("type").asText() : "AVATAR";
                CosmeticTypes type;
                try {
                    type = CosmeticTypes.valueOf(typeStr);
                } catch (Exception e) {
                    type = CosmeticTypes.AVATAR;
                }
                boolean equipped = node.has("equipped") && !node.get("equipped").isNull() && node.get("equipped").asBoolean();
                String unlockedStr = node.has("unlocked_at") && !node.get("unlocked_at").isNull() ? node.get("unlocked_at").asText() : null;
                LocalDateTime unlockedAt;
                if (unlockedStr != null) {
                    try {
                        // Postgres returns "2026-08-24 21:25:00" or with T
                        unlockedAt = LocalDateTime.parse(unlockedStr.replace(" ", "T"));
                    } catch (Exception ex) {
                        unlockedAt = LocalDateTime.now();
                    }
                } else {
                    unlockedAt = LocalDateTime.now();
                }
                list.add(InventoryItem.restore(cosmeticId, name, type, equipped, unlockedAt));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse inventory JSON: " + json, e);
        }
    }

    public static String inventoryToJson(User user) {
        try {
            List<InventoryItem> items = user.getInventory().getItems();
            List<java.util.Map<String, Object>> out = new ArrayList<>();
            for (InventoryItem item : items) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("cosmetic_id", item.cosmeticId().toString());
                m.put("equipped", item.equipped());
                m.put("unlocked_at", item.unlockedAt() != null ? item.unlockedAt().toString() : null);
                out.add(m);
            }
            return MAPPER.writeValueAsString(out);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize inventory", e);
        }
    }
}
