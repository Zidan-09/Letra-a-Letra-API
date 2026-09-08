package com.letraaletra.api.features.shop.infrastructure.persistence;

import com.letraaletra.api.features.shop.application.port.ShopPurchasePort;
import com.letraaletra.api.features.shop.application.port.ShopPurchaseResult;
import com.letraaletra.api.infrastructure.persistence.ProcedureExceptionTranslator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ShopPurchaseAdapter implements ShopPurchasePort {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public ShopPurchaseAdapter(@Autowired(required = false) JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ShopPurchaseResult purchase(UUID userId, UUID offerId) {
        if (jdbcTemplate == null) {
            throw new IllegalStateException("JdbcTemplate not available");
        }
        try {
            String json = jdbcTemplate.queryForObject(
                    "SELECT sp_buy_offer(?, ?)",
                    String.class,
                    userId, offerId
            );
            return parse(json);
        } catch (DataAccessException ex) {
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private ShopPurchaseResult parse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            List<UUID> transactionIds = new ArrayList<>();
            JsonNode ids = root.get("transaction_ids");
            if (ids != null && ids.isArray()) {
                for (JsonNode id : ids) {
                    transactionIds.add(UUID.fromString(id.asText()));
                }
            }
            long newSoftCoins = root.has("new_soft_coins") ? root.get("new_soft_coins").asLong() : 0L;
            long newHardGems = root.has("new_hard_gems") ? root.get("new_hard_gems").asLong() : 0L;
            return new ShopPurchaseResult(List.copyOf(transactionIds), newSoftCoins, newHardGems);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to parse sp_buy_offer result", ex);
        }
    }
}

