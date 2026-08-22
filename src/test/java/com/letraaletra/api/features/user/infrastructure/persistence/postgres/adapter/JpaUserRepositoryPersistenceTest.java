package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa.SpringDataCosmeticRepository;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper.CosmeticMapper;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;
import com.letraaletra.api.features.user.domain.inventory.InventoryItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaUserRepository.class)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class JpaUserRepositoryPersistenceTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private SpringDataCosmeticRepository cosmeticRepository;

    private Cosmetic avatar;

    @BeforeEach
    void setUp() {
        avatar = Cosmetic.create("avatar-test", CosmeticTypes.AVATAR, "assets/avatar.png");
        cosmeticRepository.save(CosmeticMapper.toEntity(avatar));
    }

    private User createUserAfterMatch(String nickname, boolean winner) {
        User user = UserFactory.createLocal(nickname, nickname + "@test.com", "hash");

        user.registerMatchResult(winner);
        user.getWallet().add(CoinType.SOFT, 300);
        user.getWallet().add(CoinType.HARD, 25);
        user.getInventory().unlock(avatar);

        return user;
    }

    @Test
    @DisplayName("saveAll deve persistir stats, wallet e inventário e mantê-los corretos após reload do banco")
    void saveAllShouldPersistFullAggregateAndSurviveDatabaseReload() {
        User winner = createUserAfterMatch("winner", true);
        User loser = createUserAfterMatch("loser", false);

        jpaUserRepository.saveAll(List.of(winner, loser));

        Optional<User> reloadedWinner = jpaUserRepository.find(winner.getUserId());
        Optional<User> reloadedLoser = jpaUserRepository.find(loser.getUserId());

        assertTrue(reloadedWinner.isPresent());
        assertTrue(reloadedLoser.isPresent());

        User persistedWinner = reloadedWinner.get();
        assertEquals(1, persistedWinner.getStats().getTotalMatches());
        assertEquals(1, persistedWinner.getStats().getTotalWins());
        assertEquals(300, persistedWinner.getWallet().getBalance().coins());
        assertEquals(25, persistedWinner.getWallet().getBalance().gems());

        List<InventoryItem> winnerItems = persistedWinner.getInventory().getItems();
        assertEquals(1, winnerItems.size());
        assertEquals(avatar.getId(), winnerItems.getFirst().cosmeticId());

        User persistedLoser = reloadedLoser.get();
        assertEquals(1, persistedLoser.getStats().getTotalMatches());
        assertEquals(0, persistedLoser.getStats().getTotalWins());
        assertEquals(300, persistedLoser.getWallet().getBalance().coins());

        Optional<User> byUsername = jpaUserRepository.findByUsername("winner");
        assertTrue(byUsername.isPresent());
        assertEquals(1, byUsername.get().getStats().getTotalWins());
    }

    @Test
    @DisplayName("regravar o agregado deve atualizar dados sem duplicar inventário")
    void resavingAggregateShouldUpdateDataWithoutDuplicatingInventory() {
        User user = createUserAfterMatch("player", false);

        jpaUserRepository.saveAll(List.of(user));

        Cosmetic banner = Cosmetic.create("banner-test", CosmeticTypes.BANNER, "assets/banner.png");
        cosmeticRepository.save(CosmeticMapper.toEntity(banner));

        user.registerMatchResult(true);
        user.getWallet().add(CoinType.SOFT, 700);
        user.getInventory().unlock(banner);
        user.leaveGame();

        jpaUserRepository.saveAll(List.of(user));

        Optional<User> reloaded = jpaUserRepository.find(user.getUserId());

        assertTrue(reloaded.isPresent());
        User persisted = reloaded.get();

        assertEquals(2, persisted.getStats().getTotalMatches());
        assertEquals(1000, persisted.getWallet().getBalance().coins());
        assertEquals(2, persisted.getInventory().getItems().size());

        long duplicatedCosmetics = persisted.getInventory().getItems().stream()
                .filter(item -> item.cosmeticId().equals(avatar.getId()))
                .count();
        assertEquals(1, duplicatedCosmetics);
    }
}
