package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaUserRepository.class)
class JpaUserRepositoryPersistenceTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private EntityManager entityManager;

    private User createUserAfterMatch(String nickname, boolean winner) {
        User user = UserFactory.createLocal(nickname, nickname + "@test.com", "hash");

        user.registerMatchResult(winner);
        user.getWallet().add(CoinType.SOFT, 300);
        user.getWallet().add(CoinType.HARD, 25);

        return user;
    }

    @Test
    @DisplayName("saveAll deve persistir stats e wallet e mantê-los corretos após reload do banco")
    void saveAllShouldPersistCoreAggregateAndSurviveDatabaseReload() {
        User winner = createUserAfterMatch("winner", true);
        User loser = createUserAfterMatch("loser", false);

        jpaUserRepository.saveAll(List.of(winner, loser));

        entityManager.flush();
        entityManager.clear();

        Optional<User> reloadedWinner = jpaUserRepository.find(winner.getUserId());
        Optional<User> reloadedLoser = jpaUserRepository.find(loser.getUserId());

        assertTrue(reloadedWinner.isPresent());
        assertTrue(reloadedLoser.isPresent());

        User persistedWinner = reloadedWinner.get();
        assertEquals(1, persistedWinner.getStats().getTotalMatches());
        assertEquals(1, persistedWinner.getStats().getTotalWins());
        assertEquals(300, persistedWinner.getWallet().getBalance().coins());
        assertEquals(25, persistedWinner.getWallet().getBalance().gems());

        User persistedLoser = reloadedLoser.get();
        assertEquals(1, persistedLoser.getStats().getTotalMatches());
        assertEquals(0, persistedLoser.getStats().getTotalWins());
        assertEquals(300, persistedLoser.getWallet().getBalance().coins());

        Optional<User> byUsername = jpaUserRepository.findByUsername("winner");
        assertTrue(byUsername.isPresent());
        assertEquals(1, byUsername.get().getStats().getTotalWins());
    }

    @Test
    @DisplayName("regravar o agregado deve atualizar dados do núcleo sem depender de inventário")
    void resavingAggregateShouldUpdateCoreData() {
        User user = createUserAfterMatch("player", false);

        jpaUserRepository.saveAll(List.of(user));
        entityManager.flush();
        entityManager.clear();

        user.registerMatchResult(true);
        user.getWallet().add(CoinType.SOFT, 700);
        user.leaveGame();

        jpaUserRepository.saveAll(List.of(user));
        entityManager.flush();
        entityManager.clear();

        Optional<User> reloaded = jpaUserRepository.find(user.getUserId());

        assertTrue(reloaded.isPresent());
        User persisted = reloaded.get();

        assertEquals(2, persisted.getStats().getTotalMatches());
        assertEquals(1000, persisted.getWallet().getBalance().coins());
    }
}
