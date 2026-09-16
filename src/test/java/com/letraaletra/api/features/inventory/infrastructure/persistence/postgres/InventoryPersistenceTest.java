package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres;

import com.letraaletra.api.features.items.domain.EffectType;
import com.letraaletra.api.features.inventory.domain.Inventory;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemEffect;
import com.letraaletra.api.features.items.domain.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.entity.ItemDefinitionJpaEntity;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity.UserItemJpaEntity;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.jpa.SpringDataItemDefinitionRepository;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.jpa.SpringDataUserItemRepository;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.mapper.ItemDefinitionJpaMapper;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.mapper.UserItemJpaMapper;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.projection.UserItemProjection;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Inventory Persistence Tests")
class InventoryPersistenceTest {

    @Autowired
    private SpringDataItemDefinitionRepository definitions;

    @Autowired
    private SpringDataUserItemRepository items;

    @Autowired
    private SpringDataUserRepository users;

    @Autowired
    private EntityManager entityManager;

    private UUID ownerId;
    private ItemDefinition avatar;
    private ItemDefinition boost;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();

        UserJpaEntity user = new UserJpaEntity();
        user.setId(ownerId);
        user.setUsername("owner-" + ownerId);
        user.setEmail(ownerId + "@test.com");
        user.setCreatedAt(LocalDateTime.now());
        user.setCanChangeNickname(true);
        users.save(user);

        avatar = ItemDefinition.create(
                "Blue Avatar",
                ItemKind.COSMETIC,
                ItemCategory.AVATAR,
                ItemContext.PROFILE,
                false,
                null,
                false,
                null,
                "/assets/avatar/blue.png"
        );
        boost = ItemDefinition.create(
                "XP Boost 50%",
                ItemKind.CONSUMABLE,
                ItemCategory.XP_BOOST,
                ItemContext.PROFILE,
                true,
                1000,
                true,
                new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60),
                null
        );
        definitions.save(ItemDefinitionJpaMapper.toEntity(avatar));
        definitions.save(ItemDefinitionJpaMapper.toEntity(boost));

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("definicao deve sobreviver ao reload com efeito e applicability preservados")
    void definitionShouldSurviveReload() {
        ItemDefinition reloadedAvatar = definitions.findByName("Blue Avatar")
                .map(ItemDefinitionJpaMapper::toDomain)
                .orElseThrow();

        assertEquals(avatar.getId(), reloadedAvatar.getId());
        assertEquals(ItemKind.COSMETIC, reloadedAvatar.getKind());
        assertEquals(ItemCategory.AVATAR, reloadedAvatar.getCategory());
        assertEquals(ItemContext.PROFILE, reloadedAvatar.getContext());
        assertEquals("/assets/avatar/blue.png", reloadedAvatar.getAssetPath());
        assertEquals(1, reloadedAvatar.getVersion());
        assertTrue(reloadedAvatar.isAvailable());

        ItemDefinition reloadedBoost = definitions.findById(boost.getId())
                .map(ItemDefinitionJpaMapper::toDomain)
                .orElseThrow();

        assertEquals(new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60), reloadedBoost.getEffect());
        assertEquals(1000, reloadedBoost.getMaxStack());
        assertTrue(reloadedBoost.canStack());
    }

    @Test
    @DisplayName("itens do usuario devem sobreviver ao reload via projection")
    void userItemsShouldSurviveReload() {
        Inventory inventory = Inventory.create(ownerId);
        inventory.grant(avatar, 1);
        inventory.grant(boost, 3);

        List<UserItemJpaEntity> entities = inventory.getItems().stream()
                .map(item -> UserItemJpaMapper.toEntity(ownerId, item))
                .toList();
        items.saveAll(entities);

        entityManager.flush();
        entityManager.clear();

        List<UserItem> reloaded = items.findItemsByOwner(ownerId).stream()
                .map(UserItemJpaMapper::toDomain)
                .toList();

        assertEquals(2, reloaded.size());

        UserItem reloadedAvatar = reloaded.stream()
                .filter(item -> item.getDefinitionId().equals(avatar.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(1, reloadedAvatar.getQuantity());
        assertFalse(reloadedAvatar.isEquipped());
        assertNotNull(reloadedAvatar.getAcquiredAt());
        assertNull(reloadedAvatar.getExpiresAt());

        UserItem reloadedBoost = reloaded.stream()
                .filter(item -> item.getDefinitionId().equals(boost.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(3, reloadedBoost.getQuantity());
    }

    @Test
    @DisplayName("deleteItemsByOwner deve remover apenas os itens do dono")
    void deleteItemsByOwnerShouldRemoveOnlyOwnerItems() {
        UUID otherOwner = UUID.randomUUID();
        UserJpaEntity other = new UserJpaEntity();
        other.setId(otherOwner);
        other.setUsername("other-" + otherOwner);
        other.setEmail(otherOwner + "@test.com");
        other.setCreatedAt(LocalDateTime.now());
        other.setCanChangeNickname(true);
        users.save(other);

        Inventory inventory = Inventory.create(ownerId);
        inventory.grant(avatar, 1);
        Inventory otherInventory = Inventory.create(otherOwner);
        otherInventory.grant(avatar, 1);

        items.save(UserItemJpaMapper.toEntity(ownerId, inventory.getItems().get(0)));
        items.save(UserItemJpaMapper.toEntity(otherOwner, otherInventory.getItems().get(0)));
        entityManager.flush();

        items.deleteItemsByOwner(ownerId);
        entityManager.flush();

        assertTrue(items.findItemsByOwner(ownerId).isEmpty());
        assertEquals(1, items.findItemsByOwner(otherOwner).size());
    }

    @Test
    @DisplayName("mapper deve rejeitar dados persistidos invalidos e preservar efeito polimorfico")
    void mapperShouldRejectInvalidPersistedData() {
        ItemDefinitionJpaEntity invalidApplicability = ItemDefinitionJpaMapper.toEntity(avatar);
        invalidApplicability.setApplicability("PROFILE,MATCH");
        assertThrows(InvalidItemException.class, () -> ItemDefinitionJpaMapper.toDomain(invalidApplicability));

        ItemDefinitionJpaEntity unknownContext = ItemDefinitionJpaMapper.toEntity(avatar);
        unknownContext.setApplicability("UNKNOWN");
        assertThrows(InvalidItemException.class, () -> ItemDefinitionJpaMapper.toDomain(unknownContext));

        ItemDefinitionJpaEntity corruptEffect = ItemDefinitionJpaMapper.toEntity(boost);
        corruptEffect.setEffect("{broken");
        assertThrows(InvalidItemException.class, () -> ItemDefinitionJpaMapper.toDomain(corruptEffect));

        ItemDefinition nickname = ItemDefinition.create(
                "Nickname Change",
                ItemKind.CONSUMABLE,
                ItemCategory.CHANGE_NICKNAME,
                ItemContext.PROFILE,
                true,
                1000,
                true,
                new com.letraaletra.api.features.items.domain.NicknameChangeEffect(),
                null
        );
        ItemDefinition reloadedNickname = ItemDefinitionJpaMapper.toDomain(ItemDefinitionJpaMapper.toEntity(nickname));
        assertTrue(reloadedNickname.getEffect() instanceof com.letraaletra.api.features.items.domain.NicknameChangeEffect);
    }
}
