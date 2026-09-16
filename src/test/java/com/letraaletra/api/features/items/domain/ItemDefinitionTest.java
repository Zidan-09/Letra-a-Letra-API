package com.letraaletra.api.features.items.domain;

import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemDefinition Unit Tests")
class ItemDefinitionTest {

    private ItemDefinition cosmeticAvatar() {
        return ItemDefinition.create(
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
    }

    private ItemDefinition xpBoost() {
        return ItemDefinition.create(
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
        assertEquals(1000, definition.getMaxStack());
        assertEquals(new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60), definition.getEffect());
        assertTrue(definition.isApplicableTo(ItemContext.PROFILE));
    }

    @Test
    @DisplayName("restore deve preservar todos os campos")
    void restoreShouldPreserveAllFields() {
        UUID id = UUID.randomUUID();
        ItemContext context = ItemContext.MATCH;

        ItemDefinition definition = ItemDefinition.restore(
                id,
                "Board Skin",
                ItemKind.COSMETIC,
                ItemCategory.BOARD_SKIN,
                context,
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
        assertEquals(context, definition.getContext());
        assertEquals(3, definition.getVersion());
        assertFalse(definition.isAvailable());
        assertTrue(definition.isApplicableTo(ItemContext.MATCH));
        assertFalse(definition.isApplicableTo(ItemContext.PROFILE));
    }

    @Test
    @DisplayName("contexto unico deve ser exposto via getContext")
    void singleContextShouldBeExposed() {
        ItemDefinition definition = cosmeticAvatar();

        assertEquals(ItemContext.PROFILE, definition.getContext());
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
                    ItemContext.PROFILE,
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
                    ItemContext.PROFILE,
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
                    ItemContext.PROFILE,
                    false,
                    null,
                    false,
                    null,
                    "/assets/avatar/blue.png"
            ));
        }

        @Test
        @DisplayName("contexto nulo deve falhar (R1)")
        void nullContextShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Avatar",
                    ItemKind.COSMETIC,
                    ItemCategory.AVATAR,
                    null,
                    false,
                    null,
                    false,
                    null,
                    "/assets/avatar/blue.png"
            ));
        }

        @Test
        @DisplayName("consumivel MATCH deve falhar (R2)")
        void consumableMatchShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Boost",
                    ItemKind.CONSUMABLE,
                    ItemCategory.XP_BOOST,
                    ItemContext.MATCH,
                    true,
                    1000,
                    true,
                    new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60),
                    null
            ));
        }

        @Test
        @DisplayName("consumable deve ser consistente com kind CONSUMABLE (R2)")
        void consumableMustMatchKind() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Avatar",
                    ItemKind.COSMETIC,
                    ItemCategory.AVATAR,
                    ItemContext.PROFILE,
                    false,
                    null,
                    true,
                    new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60),
                    "/assets/avatar/blue.png"
            ));

            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Boost",
                    ItemKind.CONSUMABLE,
                    ItemCategory.XP_BOOST,
                    ItemContext.PROFILE,
                    true,
                    1000,
                    false,
                    null,
                    null
            ));
        }

        @Test
        @DisplayName("efeito invalido deve falhar")
        void invalidEffectShouldFail() {
            assertThrows(InvalidItemException.class, () -> new PercentageTimedEffect(null, 50, 60));
            assertThrows(InvalidItemException.class, () -> new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 0, 60));
            assertThrows(InvalidItemException.class, () -> new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 0));
            assertThrows(InvalidItemException.class, () -> new PercentageTimedEffect(EffectType.NICKNAME_CHANGE_GRANT, 1, 1));
        }

        @Test
        @DisplayName("efeito em item nao consumivel deve falhar")
        void effectOnNonConsumableShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Avatar",
                    ItemKind.COSMETIC,
                    ItemCategory.AVATAR,
                    ItemContext.PROFILE,
                    false,
                    null,
                    false,
                    new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60),
                    "/assets/avatar/blue.png"
            ));
        }

        @Test
        @DisplayName("cosmetico sem assetPath deve falhar")
        void cosmeticWithoutAssetPathShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Avatar",
                    ItemKind.COSMETIC,
                    ItemCategory.AVATAR,
                    ItemContext.PROFILE,
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
                    ItemContext.PROFILE,
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
                    ItemContext.PROFILE,
                    true,
                    0,
                    true,
                    new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60),
                    null
            ));
        }

        @Test
        @DisplayName("consumivel exige stackable=true e maxStack=1000 (R6)")
        void consumableRequiresFixedStack() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Boost",
                    ItemKind.CONSUMABLE,
                    ItemCategory.XP_BOOST,
                    ItemContext.PROFILE,
                    true,
                    10,
                    true,
                    new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60),
                    null
            ));

            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Boost",
                    ItemKind.CONSUMABLE,
                    ItemCategory.XP_BOOST,
                    ItemContext.PROFILE,
                    false,
                    null,
                    true,
                    new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60),
                    null
            ));
        }

        @Test
        @DisplayName("categoria incompativel com kind deve falhar (R3/R4)")
        void incompatibleCategoryShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Boost",
                    ItemKind.CONSUMABLE,
                    ItemCategory.AVATAR,
                    ItemContext.PROFILE,
                    true,
                    1000,
                    true,
                    new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60),
                    null
            ));

            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Avatar",
                    ItemKind.COSMETIC,
                    ItemCategory.XP_BOOST,
                    ItemContext.PROFILE,
                    false,
                    null,
                    false,
                    null,
                    "/assets/avatar/blue.png"
            ));
        }

        @Test
        @DisplayName("categoria x efeito incompativel deve falhar (R5)")
        void incompatibleCategoryEffectShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "XP Boost",
                    ItemKind.CONSUMABLE,
                    ItemCategory.XP_BOOST,
                    ItemContext.PROFILE,
                    true,
                    1000,
                    true,
                    new PercentageTimedEffect(EffectType.RANKING_POINTS_BOOST_PCT, 50, 60),
                    null
            ));

            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "XP Boost",
                    ItemKind.CONSUMABLE,
                    ItemCategory.XP_BOOST,
                    ItemContext.PROFILE,
                    true,
                    1000,
                    true,
                    new NicknameChangeEffect(),
                    null
            ));
        }

        @Test
        @DisplayName("consumivel sem efeito deve falhar (R5)")
        void consumableWithoutEffectShouldFail() {
            assertThrows(InvalidItemException.class, () -> ItemDefinition.create(
                    "Boost",
                    ItemKind.CONSUMABLE,
                    ItemCategory.XP_BOOST,
                    ItemContext.PROFILE,
                    true,
                    1000,
                    true,
                    null,
                    null
            ));
        }

        @Test
        @DisplayName("CHANGE_NICKNAME cadastra sem magnitude/duration (R5)")
        void nicknameChangeShouldSucceedWithoutMagnitude() {
            ItemDefinition definition = ItemDefinition.create(
                    "Nickname Change",
                    ItemKind.CONSUMABLE,
                    ItemCategory.CHANGE_NICKNAME,
                    ItemContext.PROFILE,
                    true,
                    1000,
                    true,
                    new NicknameChangeEffect(),
                    null
            );

            assertTrue(definition.getEffect() instanceof NicknameChangeEffect);
        }

        @Test
        @DisplayName("vinculo categoria x efeito para as 5 categorias consumiveis")
        void allConsumableCategoriesShouldBindCorrectEffect() {
            assertDoesNotThrow(() -> ItemDefinition.create(
                    "XP", ItemKind.CONSUMABLE, ItemCategory.XP_BOOST, ItemContext.PROFILE,
                    true, 1000, true, new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 10, 30), null));
            assertDoesNotThrow(() -> ItemDefinition.create(
                    "Ranking", ItemKind.CONSUMABLE, ItemCategory.RANKING_POINTS_BOOST, ItemContext.PROFILE,
                    true, 1000, true, new PercentageTimedEffect(EffectType.RANKING_POINTS_BOOST_PCT, 10, 30), null));
            assertDoesNotThrow(() -> ItemDefinition.create(
                    "Coins", ItemKind.CONSUMABLE, ItemCategory.COIN_BOOST, ItemContext.PROFILE,
                    true, 1000, true, new PercentageTimedEffect(EffectType.COIN_BOOST_PCT, 10, 30), null));
            assertDoesNotThrow(() -> ItemDefinition.create(
                    "Shield", ItemKind.CONSUMABLE, ItemCategory.RANKING_POINTS_PROTECTION, ItemContext.PROFILE,
                    true, 1000, true, new PercentageTimedEffect(EffectType.RANKING_POINTS_SHIELD, 10, 30), null));
            assertDoesNotThrow(() -> ItemDefinition.create(
                    "Nick", ItemKind.CONSUMABLE, ItemCategory.CHANGE_NICKNAME, ItemContext.PROFILE,
                    true, 1000, true, new NicknameChangeEffect(), null));
        }
    }
}
