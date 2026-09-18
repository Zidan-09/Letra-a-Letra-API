package com.letraaletra.api.features.user.domain.effect;

import com.letraaletra.api.features.user.domain.effect.effects.RankProtectionEffect;
import com.letraaletra.api.features.user.domain.exception.InvalidUserEffectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RankProtectionEffectTest {

    @Test
    @DisplayName("Deve iniciar com a quantidade de partidas informada e sem condição de remoção")
    void shouldStartWithGivenRemainingMatches() {
        RankProtectionEffect effect = new RankProtectionEffect(3);

        assertEquals(3, effect.getRemainingUses());
        assertFalse(effect.canRemove());
    }

    @Test
    @DisplayName("Deve decrementar os usos restantes a cada utilização")
    void shouldDecrementRemainingUsesOnUse() {
        RankProtectionEffect effect = new RankProtectionEffect(3);

        effect.use();
        assertEquals(2, effect.getRemainingUses());
        assertFalse(effect.canRemove());

        effect.use();
        effect.use();
        assertEquals(0, effect.getRemainingUses());
        assertTrue(effect.canRemove());
    }

    @Test
    @DisplayName("Não deve reduzir os usos restantes abaixo de zero")
    void shouldNotGoBelowZero() {
        RankProtectionEffect effect = new RankProtectionEffect(1);

        effect.use();
        effect.use();

        assertEquals(0, effect.getRemainingUses());
        assertTrue(effect.canRemove());
    }

    @Test
    @DisplayName("Deve rejeitar quantidade inicial inválida")
    void shouldRejectInvalidInitialRemaining() {
        assertThrows(InvalidUserEffectException.class, () -> new RankProtectionEffect(0));
        assertThrows(InvalidUserEffectException.class, () -> new RankProtectionEffect(-1));
    }
}
