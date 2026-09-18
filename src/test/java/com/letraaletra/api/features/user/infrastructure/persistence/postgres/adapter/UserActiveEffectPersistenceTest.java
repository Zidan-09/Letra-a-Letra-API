package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;
import com.letraaletra.api.features.user.domain.effect.effects.ExperienceBonusEffect;
import com.letraaletra.api.features.user.domain.effect.effects.RankProtectionEffect;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserActiveEffectRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserStatsRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserWalletRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("User Active Effects Persistence Tests")
class UserActiveEffectPersistenceTest {

    @Autowired
    private SpringDataUserRepository users;

    @Autowired
    private SpringDataUserStatsRepository stats;

    @Autowired
    private SpringDataUserWalletRepository wallet;

    @Autowired
    private SpringDataUserActiveEffectRepository effects;

    @Autowired
    private EntityManager entityManager;

    private JpaUserRepository repository() {
        return new JpaUserRepository(users, wallet, stats, effects, null);
    }

    @Test
    @DisplayName("Efeitos ativos devem sobreviver ao reload")
    void activeEffectsShouldSurviveReload() {
        User user = UserFactory.createLocal("Gamer", "gamer@test.com", "hash");
        user.getActiveEffects().add(new ExperienceBonusEffect(50, Instant.now().plusSeconds(3600)));
        user.getActiveEffects().add(new RankProtectionEffect(3));

        repository().save(user);
        entityManager.flush();
        entityManager.clear();

        User reloaded = repository().find(user.getUserId()).orElseThrow();

        assertTrue(reloaded.getActiveEffects().has(ExperienceBonusEffect.class));
        assertEquals(50, reloaded.getActiveEffects().find(ExperienceBonusEffect.class).orElseThrow().getBonusPercentage());
        assertEquals(3, reloaded.getActiveEffects().find(RankProtectionEffect.class).orElseThrow().getRemainingUses());
    }

    @Test
    @DisplayName("Efeito consumido com zero usos deve reidratar e ser removivel no proximo processamento")
    void consumedEffectShouldRehydrateAsRemovable() {
        User user = UserFactory.createLocal("Gamer", "gamer@test.com", "hash");
        RankProtectionEffect protection = new RankProtectionEffect(1);
        protection.use();
        user.getActiveEffects().add(protection);

        repository().save(user);
        entityManager.flush();
        entityManager.clear();

        User reloaded = repository().find(user.getUserId()).orElseThrow();

        assertTrue(reloaded.getActiveEffects().has(RankProtectionEffect.class));
        reloaded.getActiveEffects().updateEffects();
        assertTrue(reloaded.getActiveEffects().isEmpty());

        repository().save(reloaded);
        entityManager.flush();
        entityManager.clear();

        Optional<User> again = repository().find(user.getUserId());
        assertTrue(again.orElseThrow().getActiveEffects().isEmpty());
        assertTrue(effects.findByUserId(user.getUserId()).isEmpty());
    }
}
