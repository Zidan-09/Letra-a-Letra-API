package com.letraaletra.api.features.shop.application.port;

import java.util.UUID;

public interface ShopPurchasePort {
    /**
     * Executes atomic purchase via stored procedure sp_buy_offer.
     * Preserves all validations: active, not REAL, not already purchased, sufficient balance.
     * Throws domain exceptions on failure (mapped via ProcedureExceptionTranslator).
     * @return JSON result with transaction_ids, new_soft_coins, new_hard_gems
     */
    String purchase(UUID userId, UUID offerId);
}
