package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.reward.domain.ItemGrantReward;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LevelProcedureMapperTest {

    private ResultSet resultSet(String rewardsJson) throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject("level_id")).thenReturn(UUID.randomUUID());
        when(rs.getInt("level_value")).thenReturn(5);
        when(rs.getString("rewards")).thenReturn(rewardsJson);
        return rs;
    }

    @Test
    @DisplayName("ITEM com referencia nula (item deletado) deve ser ignorado em vez de falhar")
    void orphanItemRewardShouldBeSkipped() throws Exception {
        UUID rewardId = UUID.randomUUID();
        String json = "[{\"level_reward_id\":\"" + rewardId
                + "\",\"reward_type\":\"GEMS\",\"reward_reference\":null,\"quantity\":25},"
                + "{\"level_reward_id\":\"" + UUID.randomUUID()
                + "\",\"reward_type\":\"ITEM\",\"reward_reference\":null,\"quantity\":1}]";

        Level level = LevelProcedureMapper.toDomain(resultSet(json));

        assertEquals(1, level.getRewards().size());
        assertEquals(rewardId, level.getRewards().get(0).levelRewardId());
    }

    @Test
    @DisplayName("ITEM com referencia valida deve ser mapeado normalmente")
    void validItemRewardShouldBeMapped() throws Exception {
        UUID ref = UUID.randomUUID();
        String json = "[{\"level_reward_id\":\"" + UUID.randomUUID()
                + "\",\"reward_type\":\"ITEM\",\"reward_reference\":\"" + ref + "\",\"quantity\":2}]";

        Level level = LevelProcedureMapper.toDomain(resultSet(json));

        assertEquals(1, level.getRewards().size());
        assertInstanceOf(ItemGrantReward.class, level.getRewards().get(0).reward());
        assertEquals(ref, ((ItemGrantReward) level.getRewards().get(0).reward()).definitionId());
    }
}
