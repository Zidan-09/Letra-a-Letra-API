package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.reward.domain.ItemGrantReward;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfferProcedureMapperTest {

    private ResultSet resultSet(String rewardsJson) throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject("offer_id")).thenReturn(UUID.randomUUID());
        when(rs.getString("title")).thenReturn("offer");
        when(rs.getString("coin_type")).thenReturn("SOFT");
        when(rs.getBigDecimal("price")).thenReturn(new BigDecimal("100.00"));
        when(rs.getBoolean("active")).thenReturn(true);
        when(rs.getBoolean("repeatable")).thenReturn(false);
        when(rs.getBoolean("has_expiration")).thenReturn(false);
        when(rs.getTimestamp("expires_at")).thenReturn(null);
        when(rs.getTimestamp("created_at")).thenReturn(new java.sql.Timestamp(System.currentTimeMillis()));
        when(rs.getString("rewards")).thenReturn(rewardsJson);
        return rs;
    }

    @Test
    @DisplayName("ITEM com referencia nula (item deletado) deve ser ignorado em vez de falhar")
    void orphanItemRewardShouldBeSkipped() throws Exception {
        UUID rewardId = UUID.randomUUID();
        String json = "[{\"offer_reward_id\":\"" + rewardId
                + "\",\"reward_type\":\"COIN\",\"reward_reference\":null,\"quantity\":500},"
                + "{\"offer_reward_id\":\"" + UUID.randomUUID()
                + "\",\"reward_type\":\"ITEM\",\"reward_reference\":null,\"quantity\":1}]";

        Offer offer = OfferProcedureMapper.toDomain(resultSet(json));

        assertEquals(1, offer.getRewards().size());
        assertEquals(rewardId, offer.getRewards().get(0).offerRewardId());
    }

    @Test
    @DisplayName("ITEM com referencia valida deve ser mapeado normalmente")
    void validItemRewardShouldBeMapped() throws Exception {
        UUID ref = UUID.randomUUID();
        String json = "[{\"offer_reward_id\":\"" + UUID.randomUUID()
                + "\",\"reward_type\":\"ITEM\",\"reward_reference\":\"" + ref + "\",\"quantity\":2}]";

        Offer offer = OfferProcedureMapper.toDomain(resultSet(json));

        assertEquals(1, offer.getRewards().size());
        assertInstanceOf(ItemGrantReward.class, offer.getRewards().get(0).reward());
        assertEquals(ref, ((ItemGrantReward) offer.getRewards().get(0).reward()).definitionId());
    }
}
