package com.letraaletra.api.features.user.domain.effect;

import com.letraaletra.api.features.user.domain.effect.effects.CoinBonusEffect;
import com.letraaletra.api.features.user.domain.effect.effects.RankingPointsBonusEffect;
import com.letraaletra.api.features.user.domain.exception.InvalidUserEffectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BonusEffectsTest {

    @Test
    @DisplayName("RankingPointsBonus deve expirar por tempo")
    void rankingBonusShouldExpireByTime() {
        RankingPointsBonusEffect effect = new RankingPointsBonusEffect(25, Instant.now().plusSeconds(60));

        assertTrue(effect.isValid(Instant.now()));
        assertFalse(effect.canRemove());
        assertEquals(25, effect.getBonusPercentage());
    }

    @Test
    @DisplayName("RankingPointsBonus expirado deve ser removivel")
    void expiredRankingBonusShouldBeRemovable() {
        RankingPointsBonusEffect effect = new RankingPointsBonusEffect(25, Instant.now().minusSeconds(1));

        assertTrue(effect.canRemove());
    }

    @Test
    @DisplayName("CoinBonus deve expirar por tempo")
    void coinBonusShouldExpireByTime() {
        CoinBonusEffect effect = new CoinBonusEffect(30, Instant.now().plusSeconds(60));

        assertTrue(effect.isValid(Instant.now()));
        assertFalse(effect.canRemove());
        assertEquals(30, effect.getBonusPercentage());
    }

    @Test
    @DisplayName("CoinBonus expirado deve ser removivel")
    void expiredCoinBonusShouldBeRemovable() {
        CoinBonusEffect effect = new CoinBonusEffect(30, Instant.now().minusSeconds(1));

        assertTrue(effect.canRemove());
    }

    @Test
    @DisplayName("Bonus com percentual invalido deve falhar")
    void invalidBonusPercentageShouldFail() {
        assertThrows(InvalidUserEffectException.class,
                () -> new RankingPointsBonusEffect(0, Instant.now().plusSeconds(60)));
        assertThrows(InvalidUserEffectException.class,
                () -> new CoinBonusEffect(0, Instant.now().plusSeconds(60)));
    }
}
