package com.letraaletra.api.features.inventory.domain;

import com.letraaletra.api.features.inventory.domain.exception.DuplicateUniqueItemException;
import com.letraaletra.api.features.inventory.domain.exception.InapplicableContextException;
import com.letraaletra.api.features.inventory.domain.exception.InsufficientQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotAvailableException;
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
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotOwnedException;
import com.letraaletra.api.features.inventory.domain.exception.MaxStackExceededException;
import com.letraaletra.api.features.inventory.domain.exception.NonConsumableItemException;
import com.letraaletra.api.features.inventory.domain.exception.NonEquipableItemException;
import com.letraaletra.api.features.inventory.domain.policy.ConsumableConsumePolicy;
import com.letraaletra.api.features.inventory.domain.policy.CosmeticEquipPolicy;
import com.letraaletra.api.features.inventory.domain.policy.GrantPolicy;
import com.letraaletra.api.features.inventory.domain.policy.ItemPolicyRegistry;
import com.letraaletra.api.features.inventory.domain.policy.StackableGrantPolicy;
import com.letraaletra.api.features.inventory.domain.policy.UniqueGrantPolicy;
import com.letraaletra.api.features.items.domain.repository.ItemLookup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;



@DisplayName("Inventory Unit Tests")
class InventoryTest {

    private final UUID ownerId = UUID.randomUUID();

    private EquippableItem avatar(String name) {
        return EquippableItem.create(
                name,
                EquippableContext.PROFILE,
                EquippableCategory.AVATAR,
                "/assets/avatar/" + name + ".png"
        );
    }

    private EquippableItem frame() {
        return EquippableItem.create(
                "Gold Frame",
                EquippableContext.PROFILE,
                EquippableCategory.FRAME,
                "/assets/frame/gold.png"
        );
    }

    private EquippableItem boardSkin() {
        return EquippableItem.create(
                "Dark Board",
                EquippableContext.MATCH,
                EquippableCategory.BOARD,
                "/assets/board/dark.png"
        );
    }

    private EquippableItem cellSkin() {
        return EquippableItem.create(
                "Neon Cells",
                EquippableContext.MATCH,
                EquippableCategory.CELL,
                "/assets/cell/neon.png"
        );
    }

    private ConsumableItem boost() {
        return ConsumableItem.create(
                "XP Boost 50%",
                
                new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60)
        );
    }

    private ItemLookup lookupOf(Item... items) {
        Map<UUID, Item> catalog = new HashMap<>();

        for (Item item : items) {
            catalog.put(item.getId(), item);
        }

        return itemId -> {
            Item found = catalog.get(itemId);

            if (found == null) {
                throw new ItemNotFoundException();
            }

            return found;
        };
    }

    private InventoryMovement single(List<InventoryMovement> movements) {
        assertEquals(1, movements.size());
        return movements.get(0);
    }

    @Nested
    @DisplayName("grant")
    class Grant {

        @Test
        @DisplayName("primeiro grant de item unico deve gerar ACQUIRED")
        void firstUniqueGrantShouldGenerateAcquired() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");

            InventoryMovement movement = single(inventory.grant(avatar, 1));

            assertEquals(InventoryChangeKind.ACQUIRED, movement.kind());
            assertEquals(avatar.getId(), movement.itemId());
            assertNull(movement.equippedBefore());
            assertFalse(movement.equippedAfter());
            assertEquals(0, movement.quantityBefore());
            assertEquals(1, movement.quantityAfter());
            assertEquals(1, inventory.getItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("segundo grant de item unico deve falhar")
        void secondUniqueGrantShouldFail() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");
            inventory.grant(avatar, 1);

            assertThrows(DuplicateUniqueItemException.class, () -> inventory.grant(avatar, 1));
            assertEquals(1, inventory.getItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("grant de item unico com quantidade diferente de 1 deve falhar")
        void uniqueGrantWithQuantityDifferentFromOneShouldFail() {
            Inventory inventory = Inventory.create(ownerId);

            assertThrows(InvalidQuantityException.class, () -> inventory.grant(avatar("Blue"), 2));
            assertTrue(inventory.getItems().isEmpty());
        }

        @Test
        @DisplayName("grant stackavel deve acumular e gerar QUANTITY_CHANGED")
        void stackableGrantShouldAccumulate() {
            Inventory inventory = Inventory.create(ownerId);
            Item boost = boost();
            inventory.grant(boost, 1);

            InventoryMovement movement = single(inventory.grant(boost, 2));

            assertEquals(InventoryChangeKind.QUANTITY_CHANGED, movement.kind());
            assertEquals(1, movement.quantityBefore());
            assertEquals(3, movement.quantityAfter());
            assertEquals(3, inventory.getItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("grant acima do maxStack deve falhar sem alterar o saldo")
        void grantAboveMaxStackShouldFail() {
            Inventory inventory = Inventory.create(ownerId);
            Item boost = boost();
            inventory.grant(boost, 999);

            assertThrows(MaxStackExceededException.class, () -> inventory.grant(boost, 2));
            assertEquals(999, inventory.getItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("grant de item indisponivel deve falhar (R7)")
        void unavailableGrantShouldFail() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");
            avatar.disable();

            assertThrows(ItemNotAvailableException.class, () -> inventory.grant(avatar, 1));
            assertTrue(inventory.getItems().isEmpty());
        }

        @Test
        @DisplayName("grant com quantidade zerada deve falhar")
        void zeroQuantityGrantShouldFail() {
            Inventory inventory = Inventory.create(ownerId);

            assertThrows(InvalidQuantityException.class, () -> inventory.grant(boost(), 0));
        }
    }

    @Nested
    @DisplayName("consume")
    class Consume {

        @Test
        @DisplayName("consumo parcial deve gerar CONSUMED e QUANTITY_CHANGED")
        void partialConsumeShouldGenerateConsumedAndQuantityChanged() {
            Inventory inventory = Inventory.create(ownerId);
            Item boost = boost();
            inventory.grant(boost, 3);

            List<InventoryMovement> movements = inventory.consume(boost, 2, EquippableContext.PROFILE);

            assertEquals(2, movements.size());
            assertEquals(InventoryChangeKind.CONSUMED, movements.get(0).kind());
            assertEquals(3, movements.get(0).quantityBefore());
            assertEquals(1, movements.get(0).quantityAfter());
            assertEquals(InventoryChangeKind.QUANTITY_CHANGED, movements.get(1).kind());
            assertEquals(1, inventory.getItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("consumo total deve gerar CONSUMED e REMOVED e remover a linha")
        void fullConsumeShouldRemoveLine() {
            Inventory inventory = Inventory.create(ownerId);
            Item boost = boost();
            inventory.grant(boost, 2);

            List<InventoryMovement> movements = inventory.consume(boost, 2, EquippableContext.PROFILE);

            assertEquals(2, movements.size());
            assertEquals(InventoryChangeKind.CONSUMED, movements.get(0).kind());
            assertEquals(InventoryChangeKind.REMOVED, movements.get(1).kind());
            assertTrue(inventory.getItems().isEmpty());
        }

        @Test
        @DisplayName("consumo sem saldo deve falhar sem alterar os dados")
        void consumeWithoutBalanceShouldFail() {
            Inventory inventory = Inventory.create(ownerId);
            Item boost = boost();
            inventory.grant(boost, 1);

            assertThrows(InsufficientQuantityException.class,
                    () -> inventory.consume(boost, 2, EquippableContext.PROFILE));
            assertEquals(1, inventory.getItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("consumir cosmetico deve falhar")
        void consumeCosmeticShouldFail() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");
            inventory.grant(avatar, 1);

            assertThrows(NonConsumableItemException.class,
                    () -> inventory.consume(avatar, 1, EquippableContext.PROFILE));
            assertEquals(1, inventory.getItems().size());
        }

        @Test
        @DisplayName("consumo em contexto invalido deve falhar")
        void consumeInWrongContextShouldFail() {
            Inventory inventory = Inventory.create(ownerId);
            Item boost = boost();
            inventory.grant(boost, 1);

            assertThrows(InapplicableContextException.class,
                    () -> inventory.consume(boost, 1, EquippableContext.MATCH));
            assertEquals(1, inventory.getItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("consumir item nao possuido deve falhar")
        void consumeNotOwnedShouldFail() {
            Inventory inventory = Inventory.create(ownerId);

            assertThrows(ItemNotOwnedException.class,
                    () -> inventory.consume(boost(), 1, EquippableContext.PROFILE));
        }
    }

    @Nested
    @DisplayName("equip")
    class Equip {

        @Test
        @DisplayName("equipar deve gerar EQUIPPED")
        void equipShouldGenerateEquipped() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");
            inventory.grant(avatar, 1);

            InventoryMovement movement = single(inventory.equip(avatar, EquippableContext.PROFILE, lookupOf(avatar)));

            assertEquals(InventoryChangeKind.EQUIPPED, movement.kind());
            assertEquals(avatar.getId(), movement.itemId());
            assertFalse(movement.equippedBefore());
            assertTrue(movement.equippedAfter());
        }

        @Test
        @DisplayName("equipar segundo item deve gerar UNEQUIPPED do anterior e EQUIPPED do alvo")
        void equipSecondShouldUnequipFirst() {
            Inventory inventory = Inventory.create(ownerId);
            Item oldAvatar = avatar("Old");
            Item newAvatar = avatar("New");
            ItemLookup lookup = lookupOf(oldAvatar, newAvatar);
            inventory.grant(oldAvatar, 1);
            inventory.grant(newAvatar, 1);
            inventory.equip(oldAvatar, EquippableContext.PROFILE, lookup);

            List<InventoryMovement> movements = inventory.equip(newAvatar, EquippableContext.PROFILE, lookup);

            assertEquals(2, movements.size());
            assertEquals(InventoryChangeKind.UNEQUIPPED, movements.get(0).kind());
            assertEquals(oldAvatar.getId(), movements.get(0).itemId());
            assertEquals(InventoryChangeKind.EQUIPPED, movements.get(1).kind());
            assertEquals(newAvatar.getId(), movements.get(1).itemId());
        }

        @Test
        @DisplayName("categorias diferentes nao se afetam")
        void differentCategoriesShouldNotAffectEachOther() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");
            Item frame = frame();
            ItemLookup lookup = lookupOf(avatar, frame);
            inventory.grant(avatar, 1);
            inventory.grant(frame, 1);

            inventory.equip(avatar, EquippableContext.PROFILE, lookup);
            single(inventory.equip(frame, EquippableContext.PROFILE, lookup));

            assertEquals(2, inventory.getEquipped(EquippableContext.PROFILE, lookup).size());
        }

        @Test
        @DisplayName("re-equipar o mesmo item deve ser idempotente")
        void reEquipShouldBeIdempotent() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");
            ItemLookup lookup = lookupOf(avatar);
            inventory.grant(avatar, 1);

            single(inventory.equip(avatar, EquippableContext.PROFILE, lookup));

            assertTrue(inventory.equip(avatar, EquippableContext.PROFILE, lookup).isEmpty());
        }

        @Test
        @DisplayName("PROFILE e MATCH devem ser independentes")
        void profileAndMatchShouldBeIndependent() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");
            Item boardSkin = boardSkin();
            ItemLookup lookup = lookupOf(avatar, boardSkin);
            inventory.grant(avatar, 1);
            inventory.grant(boardSkin, 1);

            inventory.equip(avatar, EquippableContext.PROFILE, lookup);
            inventory.equip(boardSkin, EquippableContext.MATCH, lookup);

            List<UserItem> profile = inventory.getEquipped(EquippableContext.PROFILE, lookup);
            List<UserItem> match = inventory.getEquipped(EquippableContext.MATCH, lookup);

            assertEquals(1, profile.size());
            assertEquals(avatar.getId(), profile.get(0).getItemId());
            assertEquals(1, match.size());
            assertEquals(boardSkin.getId(), match.get(0).getItemId());
        }

        @Test
        @DisplayName("nova categoria deve funcionar sem alteracao de codigo")
        void newCategoryShouldWorkWithoutCodeChanges() {
            Inventory inventory = Inventory.create(ownerId);
            Item cellSkin = cellSkin();
            ItemLookup lookup = lookupOf(cellSkin);
            inventory.grant(cellSkin, 1);

            single(inventory.equip(cellSkin, EquippableContext.MATCH, lookup));

            assertEquals(1, inventory.getEquipped(EquippableContext.MATCH, lookup).size());
            assertTrue(inventory.getEquipped(EquippableContext.PROFILE, lookup).isEmpty());
        }

        @Test
        @DisplayName("equipar consumivel deve falhar")
        void equipConsumableShouldFail() {
            Inventory inventory = Inventory.create(ownerId);
            Item boost = boost();
            inventory.grant(boost, 1);

            assertThrows(NonEquipableItemException.class,
                    () -> inventory.equip(boost, EquippableContext.PROFILE, lookupOf(boost)));
        }

        @Test
        @DisplayName("equipar em contexto invalido deve falhar")
        void equipInWrongContextShouldFail() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");
            inventory.grant(avatar, 1);

            assertThrows(InapplicableContextException.class,
                    () -> inventory.equip(avatar, EquippableContext.MATCH, lookupOf(avatar)));
            assertTrue(inventory.getEquipped(EquippableContext.PROFILE, lookupOf(avatar)).isEmpty());
        }

        @Test
        @DisplayName("equipar item nao possuido deve falhar")
        void equipNotOwnedShouldFail() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");

            assertThrows(ItemNotOwnedException.class,
                    () -> inventory.equip(avatar, EquippableContext.PROFILE, lookupOf(avatar)));
        }

        @Test
        @DisplayName("definicao desconhecida de item equipado deve falhar explicito (D4)")
        void unknownSiblingDefinitionShouldFailExplicitly() {
            UUID unknownId = UUID.randomUUID();
            UserItem unknown = UserItem.restore(ownerId, unknownId, 1, true, LocalDateTime.now(), null);
            Inventory inventory = Inventory.restore(ownerId, List.of(unknown));
            Item avatar = avatar("Blue");
            inventory.grant(avatar, 1);

            assertThrows(ItemNotFoundException.class,
                    () -> inventory.equip(avatar, EquippableContext.PROFILE, lookupOf(avatar)));
        }
    }

    @Nested
    @DisplayName("revoke")
    class Revoke {

        @Test
        @DisplayName("revogar item nao equipado deve gerar apenas REMOVED")
        void revokeUnequippedShouldGenerateOnlyRemoved() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");
            inventory.grant(avatar, 1);

            InventoryMovement movement = single(inventory.revoke(avatar, lookupOf(avatar)));

            assertEquals(InventoryChangeKind.REMOVED, movement.kind());
            assertFalse(movement.equippedBefore());
            assertTrue(inventory.getItems().isEmpty());
        }

        @Test
        @DisplayName("revogar item equipado deve gerar REMOVED e fallback EQUIPPED")
        void revokeEquippedShouldFallback() {
            Inventory inventory = Inventory.create(ownerId);
            Item equipped = avatar("Equipped");
            Item fallback = avatar("Fallback");
            ItemLookup lookup = lookupOf(equipped, fallback);
            inventory.grant(equipped, 1);
            inventory.grant(fallback, 1);
            inventory.equip(equipped, EquippableContext.PROFILE, lookup);

            List<InventoryMovement> movements = inventory.revoke(equipped, lookup);

            assertEquals(2, movements.size());
            assertEquals(InventoryChangeKind.REMOVED, movements.get(0).kind());
            assertTrue(movements.get(0).equippedBefore());
            assertEquals(InventoryChangeKind.EQUIPPED, movements.get(1).kind());
            assertEquals(fallback.getId(), movements.get(1).itemId());
        }

        @Test
        @DisplayName("revogar item equipado sem fallback deve gerar apenas REMOVED")
        void revokeEquippedWithoutFallbackShouldGenerateOnlyRemoved() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");
            ItemLookup lookup = lookupOf(avatar);
            inventory.grant(avatar, 1);
            inventory.equip(avatar, EquippableContext.PROFILE, lookup);

            InventoryMovement movement = single(inventory.revoke(avatar, lookup));

            assertEquals(InventoryChangeKind.REMOVED, movement.kind());
            assertTrue(movement.equippedBefore());
        }

        @Test
        @DisplayName("revogar item nao possuido deve falhar")
        void revokeNotOwnedShouldFail() {
            Inventory inventory = Inventory.create(ownerId);
            Item avatar = avatar("Blue");

            assertThrows(ItemNotOwnedException.class, () -> inventory.revoke(avatar, lookupOf(avatar)));
        }
    }

    @Nested
    @DisplayName("policies")
    class Policies {

        @Test
        @DisplayName("policies isoladas devem decidir sem depender do agregado")
        void isolatedPoliciesShouldDecide() {
            EquippableItem cosmetic = avatar("Blue");
            UserItem owned = UserItem.create(ownerId, UUID.randomUUID(), 2);

            assertDoesNotThrow(() -> new UniqueGrantPolicy().checkGrant(cosmetic, null, 1));
            assertThrows(DuplicateUniqueItemException.class,
                    () -> new UniqueGrantPolicy().checkGrant(cosmetic, owned, 1));
            assertThrows(NonConsumableItemException.class,
                    () -> new ConsumableConsumePolicy().checkConsume(cosmetic, owned, 1, EquippableContext.PROFILE));
            assertDoesNotThrow(() -> new CosmeticEquipPolicy().checkEquip(cosmetic, owned, EquippableContext.PROFILE));
            assertThrows(ItemNotOwnedException.class,
                    () -> new CosmeticEquipPolicy().checkEquip(cosmetic, null, EquippableContext.PROFILE));
        }

        @Test
        @DisplayName("registry customizado deve valer sem alterar o agregado")
        void customRegistryShouldApplyWithoutChangingAggregate() {
            GrantPolicy cappedAtFive = (definition, existing, quantity) -> {
                if (quantity < 1) {
                    throw new InvalidQuantityException();
                }

                int owned = existing == null ? 0 : existing.getQuantity();

                if (owned + quantity > 5) {
                    throw new MaxStackExceededException();
                }
            };

            ItemPolicyRegistry custom = new ItemPolicyRegistry(
                    Map.of(ItemKind.EQUIPPABLE, new UniqueGrantPolicy(), ItemKind.CONSUMABLE, cappedAtFive),
                    Map.of(ItemKind.EQUIPPABLE, new ConsumableConsumePolicy(), ItemKind.CONSUMABLE, new ConsumableConsumePolicy()),
                    Map.of(ItemKind.EQUIPPABLE, new CosmeticEquipPolicy(), ItemKind.CONSUMABLE, new CosmeticEquipPolicy())
            );

            Inventory inventory = Inventory.create(ownerId, custom);
            Item unlimited = boost();

            assertThrows(MaxStackExceededException.class, () -> inventory.grant(unlimited, 6));
            single(inventory.grant(unlimited, 5));
        }

        @Test
        @DisplayName("stackable grant policy deve respeitar o limite de 1000")
        void stackablePolicyShouldRespectMaxStack() {
            ConsumableItem boost = boost();
            UserItem owned = UserItem.create(ownerId, UUID.randomUUID(), 999);

            assertDoesNotThrow(() -> new StackableGrantPolicy().checkGrant(boost, owned, 1));
            assertThrows(MaxStackExceededException.class,
                    () -> new StackableGrantPolicy().checkGrant(boost, owned, 2));
        }
    }
}
