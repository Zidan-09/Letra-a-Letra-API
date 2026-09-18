package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres;

import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.item.ItemKind;
import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.effect.ItemEffect;
import com.letraaletra.api.features.items.domain.effect.EffectType;
import com.letraaletra.api.features.items.domain.effect.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.effect.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.catalog.ItemFilter;
import com.letraaletra.api.features.items.domain.catalog.ItemsPage;
import com.letraaletra.api.features.inventory.domain.Inventory;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.entity.ItemJpaEntity;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity.UserItemJpaEntity;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.jpa.SpringDataItemRepository;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.jpa.SpringDataUserItemRepository;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.mapper.ItemJpaMapper;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.mapper.UserItemJpaMapper;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Inventory Persistence Tests")
class InventoryPersistenceTest {

    @Autowired
    private SpringDataItemRepository definitions;

    @Autowired
    private SpringDataUserItemRepository items;

    @Autowired
    private SpringDataUserRepository users;

    @Autowired
    private EntityManager entityManager;

    private UUID ownerId;
    private EquippableItem avatar;
    private ConsumableItem boost;

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

        avatar = EquippableItem.create(
                "Blue Avatar",
                EquippableContext.PROFILE,
                EquippableCategory.AVATAR,
                "/assets/avatar/blue.png"
        );
        boost = ConsumableItem.create(
                "XP Boost 50%",
                
                new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60)
        );
        definitions.save(ItemJpaMapper.toEntity(avatar));
        definitions.save(ItemJpaMapper.toEntity(boost));

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("item deve sobreviver ao reload com efeito e contexto preservados")
    void itemShouldSurviveReload() {
        Item reloadedAvatar = definitions.findByName("Blue Avatar")
                .map(ItemJpaMapper::toDomain)
                .orElseThrow();

        assertTrue(reloadedAvatar instanceof EquippableItem);
        EquippableItem reloaded = (EquippableItem) reloadedAvatar;
        assertEquals(avatar.getId(), reloaded.getId());
        assertEquals(EquippableCategory.AVATAR, reloaded.getCategory());
        assertEquals(EquippableContext.PROFILE, reloaded.getContext());
        assertEquals("/assets/avatar/blue.png", reloaded.getAssetPath());
        assertEquals(1, reloaded.getVersion());
        assertTrue(reloaded.isAvailable());

        Item reloadedBoost = definitions.findById(boost.getId())
                .map(ItemJpaMapper::toDomain)
                .orElseThrow();

        assertTrue(reloadedBoost instanceof ConsumableItem);
        ConsumableItem reloadedConsumable = (ConsumableItem) reloadedBoost;
        assertEquals(new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60), reloadedConsumable.getEffect());
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
                .filter(item -> item.getItemId().equals(avatar.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(1, reloadedAvatar.getQuantity());
        assertFalse(reloadedAvatar.isEquipped());
        assertNotNull(reloadedAvatar.getAcquiredAt());
        assertNull(reloadedAvatar.getExpiresAt());

        UserItem reloadedBoost = reloaded.stream()
                .filter(item -> item.getItemId().equals(boost.getId()))
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
        ItemJpaEntity invalidApplicability = ItemJpaMapper.toEntity(avatar);
        invalidApplicability.setApplicability("PROFILE,MATCH");
        assertThrows(InvalidItemException.class, () -> ItemJpaMapper.toDomain(invalidApplicability));

        ItemJpaEntity unknownContext = ItemJpaMapper.toEntity(avatar);
        unknownContext.setApplicability("UNKNOWN");
        assertThrows(InvalidItemException.class, () -> ItemJpaMapper.toDomain(unknownContext));

        ItemJpaEntity corruptEffect = ItemJpaMapper.toEntity(boost);
        corruptEffect.setEffect("{broken");
        assertThrows(InvalidItemException.class, () -> ItemJpaMapper.toDomain(corruptEffect));

        ConsumableItem nickname = ConsumableItem.create(
                "Nickname Change",
                
                new com.letraaletra.api.features.items.domain.NicknameChangeEffect()
        );
        Item reloadedNickname = ItemJpaMapper.toDomain(ItemJpaMapper.toEntity(nickname));
        assertTrue(reloadedNickname instanceof ConsumableItem);
        ConsumableItem reloaded = (ConsumableItem) reloadedNickname;
        assertTrue(reloaded.getEffect() instanceof com.letraaletra.api.features.items.domain.NicknameChangeEffect);
    }
}
