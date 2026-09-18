package com.letraaletra.api.features.items.domain;

import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ConsumableItem Unit Tests")
class ConsumableItemTest {

    private PercentageTimedEffect xpEffect() {
        return new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60);
    }

    @Test
    @DisplayName("create deve gerar id, versao 1 e disponivel")
    void createShouldGenerateDefaults() {
        ConsumableItem item = ConsumableItem.create(
                "XP Boost 50%",
                ItemCategory.XP_BOOST,
                EquippableContext.PROFILE,
                xpEffect()
        );

        assertNotNull(item.getId());
        assertEquals(1, item.getVersion());
        assertTrue(item.isAvailable());
        assertEquals("XP Boost 50%", item.getName());
        assertEquals(ItemCategory.XP_BOOST, item.getCategory());
        assertEquals(EquippableContext.PROFILE, item.getContext());
        assertEquals(xpEffect(), item.getEffect());
    }

    @Test
    @DisplayName("restore deve preservar todos os campos")
    void restoreShouldPreserveAllFields() {
        UUID id = UUID.randomUUID();

        ConsumableItem item = ConsumableItem.restore(
                id,
                "XP Boost 50%",
                2,
                false,
                ItemCategory.XP_BOOST,
                EquippableContext.PROFILE,
                xpEffect()
        );

        assertEquals(id, item.getId());
        assertEquals(2, item.getVersion());
        assertFalse(item.isAvailable());
        assertEquals(xpEffect(), item.getEffect());
    }

    @Test
    @DisplayName("contexto diferente de PROFILE deve falhar")
    void nonProfileContextShouldFail() {
        assertThrows(InvalidItemException.class, () -> ConsumableItem.create(
                "XP Boost 50%",
                ItemCategory.XP_BOOST,
                EquippableContext.MATCH,
                xpEffect()
        ));

        assertThrows(InvalidItemException.class, () -> ConsumableItem.create(
                "XP Boost 50%",
                ItemCategory.XP_BOOST,
                null,
                xpEffect()
        ));
    }

    @Test
    @DisplayName("categoria nao consumivel ou nula deve falhar")
    void nonConsumableOrNullCategoryShouldFail() {
        assertThrows(InvalidItemException.class, () -> ConsumableItem.create(
                "Avatar",
                ItemCategory.AVATAR,
                EquippableContext.PROFILE,
                xpEffect()
        ));

        assertThrows(InvalidItemException.class, () -> ConsumableItem.create(
                "Boost",
                null,
                EquippableContext.PROFILE,
                xpEffect()
        ));
    }

    @Test
    @DisplayName("efeito nulo deve falhar")
    void nullEffectShouldFail() {
        assertThrows(InvalidItemException.class, () -> ConsumableItem.create(
                "Boost",
                ItemCategory.XP_BOOST,
                EquippableContext.PROFILE,
                null
        ));
    }

    @Test
    @DisplayName("efeito incompativel com a categoria deve falhar")
    void incompatibleEffectShouldFail() {
        assertThrows(InvalidItemException.class, () -> ConsumableItem.create(
                "XP Boost",
                ItemCategory.XP_BOOST,
                EquippableContext.PROFILE,
                new PercentageTimedEffect(EffectType.RANKING_POINTS_BOOST_PCT, 50, 60)
        ));

        assertThrows(InvalidItemException.class, () -> ConsumableItem.create(
                "XP Boost",
                ItemCategory.XP_BOOST,
                EquippableContext.PROFILE,
                new NicknameChangeEffect()
        ));
    }

    @Test
    @DisplayName("troca de nome deve aceitar efeito instantaneo")
    void nicknameChangeShouldAcceptInstantEffect() {
        ConsumableItem item = ConsumableItem.create(
                "Nickname Change",
                ItemCategory.CHANGE_NICKNAME,
                EquippableContext.PROFILE,
                new NicknameChangeEffect()
        );

        assertTrue(item.getEffect() instanceof NicknameChangeEffect);
    }
}
