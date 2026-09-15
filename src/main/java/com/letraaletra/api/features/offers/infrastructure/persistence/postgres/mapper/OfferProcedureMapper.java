package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.reward.domain.HardGemsReward;
import com.letraaletra.api.features.reward.domain.ItemGrantReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class OfferProcedureMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private OfferProcedureMapper() {}

    public static Offer toDomain(ResultSet rs) throws SQLException {
        UUID offerId = (UUID) rs.getObject("offer_id");
        String title = rs.getString("title");
        String coinTypeStr = rs.getString("coin_type");
        CoinType coinType = CoinType.valueOf(coinTypeStr);
        BigDecimal price = rs.getBigDecimal("price");
        boolean active = rs.getBoolean("active");
        boolean repeatable = rs.getBoolean("repeatable");
        boolean hasExpiration = rs.getBoolean("has_expiration");
        java.sql.Timestamp expiresTs = rs.getTimestamp("expires_at");
        java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
        String rewardsJson = rs.getString("rewards");

        LocalDateTime expiresAt = expiresTs != null ? expiresTs.toLocalDateTime() : null;
        LocalDateTime createdAt = createdTs != null ? createdTs.toLocalDateTime() : LocalDateTime.now();

        List<OfferReward> rewards = parseRewards(rewardsJson);

        return Offer.restore(offerId, title, coinType, price, rewards, active, repeatable, hasExpiration, expiresAt, createdAt);
    }

    private static List<OfferReward> parseRewards(String json) {
        if (json == null || json.isBlank() || json.equals("[]") || json.equals("null")) return List.of();
        try {
            JsonNode array = MAPPER.readTree(json);
            if (!array.isArray()) return List.of();
            List<OfferReward> list = new ArrayList<>();
            for (JsonNode node : array) {
                String idStr = node.has("offer_reward_id") && !node.get("offer_reward_id").isNull() ? node.get("offer_reward_id").asText() : null;
                UUID rewardId = idStr != null ? UUID.fromString(idStr) : UUID.randomUUID();
                String rewardTypeStr = node.get("reward_type").asText();
                RewardType rewardType = RewardType.valueOf(rewardTypeStr);
                String refStr = node.has("reward_reference") && !node.get("reward_reference").isNull() ? node.get("reward_reference").asText() : null;
                UUID ref = (refStr != null && !refStr.equals("null") && !refStr.isBlank()) ? UUID.fromString(refStr) : null;
                int quantity = node.has("quantity") && !node.get("quantity").isNull() ? node.get("quantity").asInt() : 1;

                Reward reward;
                switch (rewardType) {
                    case COIN -> reward = new SoftCoinsReward(quantity);
                    case GEMS -> reward = new HardGemsReward(quantity);
                    case ITEM -> {
                        if (ref == null) {
                            continue;
                        }
                        reward = new ItemGrantReward(ref, quantity);
                    }
                    default -> throw new IllegalArgumentException("Unknown reward type: " + rewardType);
                }
                list.add(new OfferReward(rewardId, reward));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse rewards JSON: " + json, e);
        }
    }

    public static String rewardsToJson(Offer offer) {
        try {
            List<java.util.Map<String, Object>> out = new ArrayList<>();
            for (OfferReward r : offer.getRewards()) {
                java.util.Map<String, Object> m = new java.util.HashMap<>();
                m.put("offer_reward_id", r.offerRewardId().toString());
                Reward reward = r.reward();
                switch (reward) {
                    case ItemGrantReward ir -> {
                        m.put("reward_type", "ITEM");
                        m.put("reward_reference", ir.definitionId().toString());
                        m.put("quantity", ir.quantity());
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
            throw new RuntimeException("Failed to serialize rewards", e);
        }
    }
}
