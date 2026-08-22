package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.CosmeticTypes;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserStatsJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserWalletJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserInventoryRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserStatsRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryTest {

    @Mock private SpringDataUserRepository baseRepository;
    @Mock private SpringDataUserInventoryRepository inventoryRepository;
    @Mock private SpringDataUserWalletRepository walletRepository;
    @Mock private SpringDataUserStatsRepository statsRepository;

    private JpaUserRepository jpaUserRepository;

    @BeforeEach
    void setUp() {
        jpaUserRepository = new JpaUserRepository(
                baseRepository,
                inventoryRepository,
                walletRepository,
                statsRepository
        );
    }

    private User createUserWithMatchData(String nickname) {
        User user = UserFactory.createLocal(nickname, nickname + "@test.com", "hash");

        user.registerMatchResult(true);
        user.getWallet().add(CoinType.SOFT, 250);
        user.getWallet().add(CoinType.HARD, 10);

        Cosmetic cosmetic = Cosmetic.create("skin-" + nickname, CosmeticTypes.AVATAR, "assets/avatar.png");
        user.getInventory().unlock(cosmetic);

        return user;
    }

    @Test
    @DisplayName("saveAll deve persistir stats, wallet e inventário de cada usuário do agregado")
    void saveAllShouldPersistStatsWalletAndInventoryForEachUser() {
        User winner = createUserWithMatchData("winner");
        User loser = createUserWithMatchData("loser");

        jpaUserRepository.saveAll(List.of(winner, loser));

        verify(baseRepository, times(2)).save(any(UserJpaEntity.class));

        ArgumentCaptor<UserStatsJpaEntity> statsCaptor = ArgumentCaptor.forClass(UserStatsJpaEntity.class);
        verify(statsRepository, times(2)).save(statsCaptor.capture());
        assertEquals(1, statsCaptor.getAllValues().get(0).getTotalMatches());
        assertEquals(1, statsCaptor.getAllValues().get(0).getTotalWins());
        assertEquals(winner.getUserId(), statsCaptor.getAllValues().get(0).getUserId());
        assertEquals(loser.getUserId(), statsCaptor.getAllValues().get(1).getUserId());

        ArgumentCaptor<UserWalletJpaEntity> walletCaptor = ArgumentCaptor.forClass(UserWalletJpaEntity.class);
        verify(walletRepository, times(2)).save(walletCaptor.capture());
        assertEquals(250, walletCaptor.getAllValues().get(0).getSoftCoins());
        assertEquals(10, walletCaptor.getAllValues().get(0).getHardGems());
        assertEquals(250, walletCaptor.getAllValues().get(1).getSoftCoins());

        verify(inventoryRepository, times(2)).deleteAllByUserId(any(UUID.class));
        verify(inventoryRepository, times(2)).saveAll(anyList());
    }

    @Test
    @DisplayName("save deve persistir o inventário do usuário")
    void saveShouldPersistUserInventory() {
        User user = createUserWithMatchData("player");

        jpaUserRepository.save(user);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserInventoryJpaEntity>> inventoryCaptor =
                ArgumentCaptor.forClass((Class) List.class);

        verify(inventoryRepository).deleteAllByUserId(user.getUserId());
        verify(inventoryRepository).saveAll(inventoryCaptor.capture());

        assertEquals(1, inventoryCaptor.getValue().size());
        assertEquals(user.getUserId(), inventoryCaptor.getValue().getFirst().getUserInventoryId().getUserId());
    }
}
