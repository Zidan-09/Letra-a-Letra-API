package com.letraaletra.api.features.shop.infrastructure.persistence;

import com.letraaletra.api.features.shop.application.port.ShopPurchasePort;
import com.letraaletra.api.infrastructure.persistence.ProcedureExceptionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ShopPurchaseAdapter implements ShopPurchasePort {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ShopPurchaseAdapter(@Autowired(required = false) JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String purchase(UUID userId, UUID offerId) {
        if (jdbcTemplate == null) {
            throw new IllegalStateException("JdbcTemplate not available");
        }
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT sp_buy_offer(?, ?)",
                    String.class,
                    userId, offerId
            );
        } catch (DataAccessException ex) {
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }
}

