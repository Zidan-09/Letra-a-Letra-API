package com.letraaletra.api.features.inventory.domain;

import com.letraaletra.api.features.inventory.domain.exception.InvalidItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemDefinition Unit Tests")
class ItemDefinitionTest {

    private ItemDefinition cosmeticAvatar() {
        return ItemDefinition.create(
                "Blue Avatar",
                ItemKind.COSMETIC,
                ItemCategory.AVATAR,
                Set.of(ItemContext.PROFILE),
                false,
                null,
                false,
                null,
                "/assets/avatar/blue.png"
        );
    }

    private ItemDefinition xpBoost() {
        return ItemDefinition.create(
                "XP Boost 50%",
                ItemKind.CONSUMABLE,
                ItemCategory.XP_BOOST,
                Set.of(ItemContext.PROFILE),
                true,
                10,
                true,
                new ItemEffect(EffectType.XP_BOOST_PCT, 50, 60),
                null
        );
    }

    @Test
    @DisplayName("create deve gerar id aleatorio, versao 1 e disponivel")
    void createShouldGenerateDefaults() {
        ItemDefinition definition = cosmeticAvatar();

        assertNotNull(definition.getId());
        assertEquals(1, definition.getVersion());
        assertTrue(definition.isAvailable());
        assertEquals("Blue Avatar", definition.getName());
    }

    @Test
    @DisplayName("cosmetico migrado deve ser unico, nao consumivel e aplicavel a PROFILE")
    void migratedCosmeticShouldBeUniqueProfileItem() {
        ItemDefinition definition = cosmeticAvatar();

        assertFalse(definition.canStack());
        assertFalse(definition.isConsumable());
        assertTrue(definition.isApplicableTo(ItemContext.PROFILE));
        assertFalse(definition.isApplicableTo(ItemContext.MATCH));
        assertFalse(definition.isApplicableTo(null));
        assertNull(definition.getMaxStack());
        assertNull(definition.getEffect());
    }

    @Test
    @DisplayName("consumivel deve ser stackavel, consumivel e expor efeito")
    void consumableShouldExposeStackAndEffect() {
        ItemDefinition definition = xpBoost();

        assertTrue(definition.canStack());
        assertTrue(definition.isConsumable());
        assertEquals(10, definition.getMaxStack());
        assertEquals(new ItemEffect(EffectType.XP_BOOST_PCT, 50, 60), definition.getEffect());
        assertTrue(definition.isApplicableTo(ItemContext.PROFILE));
    }

    @Test
    @DisplayName("restore deve preservar todos os campos")
    void restoreShouldPreserveAllFields() {
        UUID id = UUID.randomUUID();
        Set<ItemContext> applicability = EnumSet.of(ItemContext.MATCH);

        ItemDefinition definition = ItemDefinition.restore(
                id,
                "Board Skin",
                ItemKind.COSMETIC,
                ItemCategory.BOARD_SKIN,
                applicability,
                false,
                null,
                false,
                null,
                "/assets/board/dark.png",
                3,
                false
        );

        assertEquals(id, definition.getId());
        assertEquals("Board Skin", definition.getName());
        assertEquals(ItemKind.COSMETIC, definition.getKind());
        assertEquals(ItemCategory.BOARD_SKIN, definition.getCategory());
        assertEquals(applicability, definition.getApplicability());
        assertEquals(3, definition.getVersion());
        assertFalse(definition.isAvailable());
        assertTrue(definition.isApplicableTo(ItemContext.MATCH));
        assertFalse(definition.isApplicableTo(ItemContext.PROFILE));
    }

    @Test
    @DisplayName("applicability deve ser copiada defensivamente")
    void applicabilityShouldBeDefensivelyCopied() {
        Set<ItemContext> applicability = EnumSet.of(ItemContext.PROFILE);

        ItemDefinition definition = ItemDefinition.create(
                "Emote",
                ItemKind.COSMETIC,
                ItemCategory.EMOTE,
                applicability,
                false,
                null,
                false,
                null,
                "/assets/emote/wave.png"
        );

        applicability.add(ItemContext.MATCH);
        definition.getApplicability().add(ItemContext.MATCH);

        assertEquals(Set.of(ItemContext.PROFILE), definition.getApplicability());
    }

    @Test
    @DisplayName("incrementVersion e setAvailable devem funcionar como em Cosmetic")
    void versionAndAvailabilityShouldBeMutable() {
        ItemDefinition definition = cosmeticAvatar();

        definition.incrementVersion();
        definition.setAvailable(false);

        assertEquals(2, definition.getVersion());
        assertFalse(definition.isAvailable());
    }

    @Nested
    @DisplayName("validacao")
    class Validation {

        @Test
        @DisplayName("nome em branco deve falhar")
        void blankNameShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "  ",
                    ItemKind.COSMETIC,
                    ItemCategory.AVATAR,
                    Set.of(ItemContext.PROFILE),
                    false,
                    null,
                    false,
                    null,
                    "/assets/avatar/blue.png"
            ));
        }

        @Test
        @DisplayName("kind ou category nulos devem falhar")
        void nullKindOrCategoryShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Avatar",
                    null,
                    ItemCategory.AVATAR,
                    Set.of(ItemContext.PROFILE),
                    false,
                    null,
                    false,
                    null,
                    "/assets/avatar/blue.png"
            ));

            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Avatar",
                    ItemKind.COSMETIC,
                    null,
                    Set.of(ItemContext.PROFILE),
                    false,
                    null,
                    false,
                    null,
                    "/assets/avatar/blue.png"
            ));
        }

        @Test
        @DisplayName("applicability vazia deve falhar")
        void emptyApplicabilityShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Avatar",
                    ItemKind.COSMETIC,
                    ItemCategory.AVATAR,
                    Set.of(),
                    false,
                    null,
                    false,
                    null,
                    "/assets/avatar/blue.png"
            ));
        }

        @Test
        @DisplayName("consumable deve ser consistente com kind CONSUMABLE (R2)")
        void consumableMustMatchKind() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Avatar",
                    ItemKind.COSMETIC,
                    ItemCategory.AVATAR,
                    Set.of(ItemContext.PROFILE),
                    false,
                    null,
                    true,
                    new ItemEffect(EffectType.XP_BOOST_PCT, 50, 60),
                    "/assets/avatar/blue.png"
            ));

            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Boost",
                    ItemKind.CONSUMABLE,
                    ItemCategory.XP_BOOST,
                    Set.of(ItemContext.PROFILE),
                    true,
                    null,
                    false,
                    null,
                    null
            ));
        }

        @Test
        @DisplayName("efeito em item nao consumivel deve falhar")
        void effectOnNonConsumableShouldFail() {
            assertThrows(InvalidItemException.class, () -> new ItemEffect(null, 50, 60));
            assertThrows(InvalidItemException.class, () -> new ItemEffect(EffectType.XP_BOOST_PCT, 0, 60));
            assertThrows(InvalidItemException.class, () -> new ItemEffect(EffectType.XP_BOOST_PCT, 50, 0));
        }

        @Test
        @DisplayName("cosmetico sem assetPath deve falhar")
        void cosmeticWithoutAssetPathShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Avatar",
                    ItemKind.COSMETIC,
                    ItemCategory.AVATAR,
                    Set.of(ItemContext.PROFILE),
                    false,
                    null,
                    false,
                    null,
                    null
            ));
        }

        @Test
        @DisplayName("maxStack inconsistente deve falhar")
        void inconsistentMaxStackShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Avatar",
                    ItemKind.COSMETIC,
                    ItemCategory.AVATAR,
                    Set.of(ItemContext.PROFILE),
                    false,
                    5,
                    false,
                    null,
                    "/assets/avatar/blue.png"
            ));

            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Boost",
                    ItemKind.CONSUMABLE,
                    ItemCategory.XP_BOOST,
                    Set.of(ItemContext.PROFILE),
                    true,
                    0,
                    true,
                    new ItemEffect(EffectType.XP_BOOST_PCT, 50, 60),
                    null
            ));
        }
    }
}
