package com.letraaletra.api.features.ranking.infrastructure.service;

import com.letraaletra.api.features.ranking.domain.UpdateRankingPoints;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;
import com.letraaletra.api.features.user.domain.effect.effects.RankProtectionEffect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateRankingPointsServiceTest {

    private final UpdateRankingPointsService service = new UpdateRankingPointsService();

    private User userWithPoints() {
        User user = UserFactory.createLocal("Gamer", "gamer@test.com", "hash");
        user.getStats().incrementPoints(3, 0);
        user.getStats().incrementPoints(3, 0);

        return user;
    }

    @Test
    @DisplayName("Deve proteger derrota consumindo um uso sem alterar os pontos")
    void shouldShieldLossConsumingOneUse() {
        User user = userWithPoints();
        int before = user.getStats().getRankingPoints();
        user.getActiveEffects().add(new RankProtectionEffect(3));

        UpdateRankingPoints result = service.handle(user, 0, 3);

        assertEquals(before, result.before());
        assertEquals(0, result.changed());
        assertEquals(before, result.after());
        assertEquals(before, user.getStats().getRankingPoints());
        assertEquals(2, user.getActiveEffects().find(RankProtectionEffect.class).orElseThrow().getRemainingUses());
    }

    @Test
    @DisplayName("Deve remover a protecao apos o ultimo uso")
    void shouldRemoveProtectionAfterLastUse() {
        User user = userWithPoints();
        user.getActiveEffects().add(new RankProtectionEffect(1));

        service.handle(user, 0, 3);

        assertTrue(user.getActiveEffects().isEmpty());
    }

    @Test
    @DisplayName("Nao deve consumir protecao em vitoria")
    void shouldNotConsumeProtectionOnWin() {
        User user = userWithPoints();
        user.getActiveEffects().add(new RankProtectionEffect(3));

        UpdateRankingPoints result = service.handle(user, 3, 0);

        assertTrue(result.changed() > 0);
        assertEquals(3, user.getActiveEffects().find(RankProtectionEffect.class).orElseThrow().getRemainingUses());
    }

    @Test
    @DisplayName("Sem protecao a derrota deve reduzir os pontos normalmente")
    void shouldReducePointsNormallyWithoutProtection() {
        User user = userWithPoints();
        int before = user.getStats().getRankingPoints();

        UpdateRankingPoints result = service.handle(user, 0, 3);

        assertTrue(result.changed() < 0);
        assertEquals(before + result.changed(), user.getStats().getRankingPoints());
    }
}
