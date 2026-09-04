package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.LevelReward;
import com.letraaletra.api.features.reward.domain.CosmeticReward;
import com.letraaletra.api.features.reward.domain.HardGemsReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class LevelProcedureMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private LevelProcedureMapper() {}

    public static Level toDomain(ResultSet rs) throws SQLException {
        UUID levelId = (UUID) rs.getObject("level_id");
        int levelValue = rs.getInt("level_value");
        String rewardsJson = rs.getString("rewards");
        List<LevelReward> rewards = parseRewards(rewardsJson);
        return new Level(levelId, levelValue, rewards);
    }

    private static List<LevelReward> parseRewards(String json) {
        if (json == null || json.isBlank() || json.equals("[]") || json.equals("null")) return List.of();
        try {
            JsonNode array = MAPPER.readTree(json);
            if (!array.isArray()) return List.of();
            List<LevelReward> list = new ArrayList<>();
            for (JsonNode node : array) {
                String idStr = node.has("level_reward_id") && !node.get("level_reward_id").isNull() ? node.get("level_reward_id").asText() : null;
                UUID rewardId = idStr != null ? UUID.fromString(idStr) : UUID.randomUUID();
                String rewardTypeStr = node.get("reward_type").asText();
                RewardType rewardType = RewardType.valueOf(rewardTypeStr);
                String refStr = node.has("reward_reference") && !node.get("reward_reference").isNull() ? node.get("reward_reference").asText() : null;
                UUID ref = (refStr != null && !refStr.equals("null") && !refStr.isBlank()) ? UUID.fromString(refStr) : null;
                int quantity = node.has("quantity") && !node.get("quantity").isNull() ? node.get("quantity").asInt() : 1;

                Reward reward;
                switch (rewardType) {
                    case COSMETIC -> {
                        JsonNode cosNode = node.get("cosmetic");
                        if (cosNode == null || cosNode.isNull()) {
                            throw new com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException();
                        }
                        UUID cosId = UUID.fromString(cosNode.get("cosmetic_id").asText());
                        String name = cosNode.get("name").asText();
                        CosmeticTypes type = CosmeticTypes.valueOf(cosNode.get("type").asText());
                        String asset = cosNode.has("asset_path") && !cosNode.get("asset_path").isNull() ? cosNode.get("asset_path").asText() : "";
                        int version = cosNode.has("version") && !cosNode.get("version").isNull() ? cosNode.get("version").asInt() : 1;
                        boolean available = cosNode.has("available") && !cosNode.get("available").isNull() && cosNode.get("available").asBoolean();
                        Cosmetic cosmetic = Cosmetic.restore(cosId, name, type, asset, version, available);
                        reward = new CosmeticReward(cosmetic);
                    }
                    case COIN -> reward = new SoftCoinsReward(quantity);
                    case GEMS -> reward = new HardGemsReward(quantity);
                    default -> throw new IllegalArgumentException("Unknown reward type: " + rewardType);
                }
                list.add(new LevelReward(rewardId, reward));
            }
            return list;
        } catch (com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse level rewards JSON: " + json, e);
        }
    }

    public static String rewardsToJson(Level level) {
        try {
            List<java.util.Map<String, Object>> out = new ArrayList<>();
            for (LevelReward r : level.getRewards()) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("level_reward_id", r.levelRewardId().toString());
                Reward reward = r.reward();
                switch (reward) {
                    case CosmeticReward cr -> {
                        m.put("reward_type", "COSMETIC");
                        m.put("reward_reference", cr.cosmetic().getId().toString());
                        m.put("quantity", 1);
                    }
                    case SoftCoinsReward sr -> {
                        m.put("reward_type", "COIN");
                        m.put("reward_reference", null);
                        m.put("quantity", sr.amount());
                    }
                    case HardGemsReward hr -> {
                        m.put("reward_type", "GEMS");
                        m.put("reward_reference", null);
                        m.put("quantity", hr.amount());
                    }
                    default -> throw new IllegalArgumentException("Unknown reward");
                }
                out.add(m);
            }
            return MAPPER.writeValueAsString(out);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize level rewards", e);
        }
    }
}
