package com.letraaletra.api.features.items.domain;

import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EquippableItem Unit Tests")
class EquippableItemTest {

    @ParameterizedTest(name = "{0} + {1} deve ser valido")
    @CsvSource({
            "PROFILE, AVATAR",
            "PROFILE, BANNER",
            "PROFILE, FRAME",
            "MATCH, BOARD_SKIN",
            "MATCH, CELL_SKIN",
            "MATCH, EMOTE"
    })
    @DisplayName("combinacoes validas de contexto e categoria")
    void validContextCategoryCombinations(EquippableContext context, ItemCategory category) {
        EquippableItem item = EquippableItem.create(
                "Item",
                context,
                category,
                "assets/item.png"
        );

        assertEquals(context, item.getContext());
        assertEquals(category, item.getCategory());
    }

    @ParameterizedTest(name = "{0} + {1} deve falhar")
    @CsvSource({
            "PROFILE, BOARD_SKIN",
            "PROFILE, CELL_SKIN",
            "PROFILE, EMOTE",
            "MATCH, AVATAR",
            "MATCH, BANNER",
            "MATCH, FRAME"
    })
    @DisplayName("combinacoes cruzadas de contexto e categoria devem falhar")
    void crossedContextCategoryCombinationsShouldFail(EquippableContext context, ItemCategory category) {
        assertThrows(InvalidItemException.class, () -> EquippableItem.create(
                "Item",
                context,
                category,
                "assets/item.png"
        ));
    }

    @Test
    @DisplayName("categoria consumivel deve falhar")
    void consumableCategoryShouldFail() {
        assertThrows(InvalidItemException.class, () -> EquippableItem.create(
                "Boost",
                EquippableContext.PROFILE,
                ItemCategory.XP_BOOST,
                null
        ));
    }

    @Test
    @DisplayName("contexto ou categoria nulos devem falhar")
    void nullContextOrCategoryShouldFail() {
        assertThrows(InvalidItemException.class, () -> EquippableItem.create(
                "Item",
                null,
                ItemCategory.AVATAR,
                "avatars/item.png"
        ));

        assertThrows(InvalidItemException.class, () -> EquippableItem.create(
                "Item",
                EquippableContext.PROFILE,
                null,
                "avatars/item.png"
        ));
    }

    @Test
    @DisplayName("assetPath em branco deve falhar")
    void blankAssetPathShouldFail() {
        assertThrows(InvalidItemException.class, () -> EquippableItem.create(
                "Item",
                EquippableContext.PROFILE,
                ItemCategory.AVATAR,
                null
        ));

        assertThrows(InvalidItemException.class, () -> EquippableItem.create(
                "Item",
                EquippableContext.PROFILE,
                ItemCategory.AVATAR,
                "  "
        ));
    }

    @Test
    @DisplayName("assetPath absoluto com esquema deve falhar")
    void absoluteAssetPathShouldFail() {
        assertThrows(InvalidItemException.class, () -> EquippableItem.create(
                "Item",
                EquippableContext.PROFILE,
                ItemCategory.AVATAR,
                "https://cdn.example.com/avatars/item.png"
        ));
    }

    @Test
    @DisplayName("assetPath relativo deve ser aceito")
    void relativeAssetPathShouldPass() {
        EquippableItem item = EquippableItem.create(
                "Item",
                EquippableContext.PROFILE,
                ItemCategory.AVATAR,
                "avatars/item.png"
        );

        assertEquals("avatars/item.png", item.getAssetPath());

        item.setAssetPath("avatars/other.png");
        assertEquals("avatars/other.png", item.getAssetPath());
    }

    @Test
    @DisplayName("setAssetPath invalido deve falhar sem alterar o atual")
    void invalidSetAssetPathShouldFail() {
        EquippableItem item = EquippableItem.create(
                "Item",
                EquippableContext.PROFILE,
                ItemCategory.AVATAR,
                "avatars/item.png"
        );

        assertThrows(InvalidItemException.class,
                () -> item.setAssetPath("https://cdn.example.com/avatars/other.png"));
        assertEquals("avatars/item.png", item.getAssetPath());
    }
}
