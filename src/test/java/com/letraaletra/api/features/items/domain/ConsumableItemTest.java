package com.letraaletra.api.features.items.domain;

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
                xpEffect()
        );

        assertNotNull(item.getId());
        assertEquals(1, item.getVersion());
        assertTrue(item.isAvailable());
        assertEquals("XP Boost 50%", item.getName());
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
                xpEffect()
        );

        assertEquals(id, item.getId());
        assertEquals(2, item.getVersion());
        assertFalse(item.isAvailable());
        assertEquals(xpEffect(), item.getEffect());
    }

    @Test
    @DisplayName("troca de nome deve aceitar efeito instantaneo")
    void nicknameChangeShouldAcceptInstantEffect() {
        ConsumableItem item = ConsumableItem.create(
                "Nickname Change",
                new NicknameChangeEffect()
        );

        assertTrue(item.getEffect() instanceof NicknameChangeEffect);
    }
}
