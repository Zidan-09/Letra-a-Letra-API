package com.letraaletra.api.features.user.domain.effect;

import com.letraaletra.api.features.user.domain.effect.effects.ExperienceBonusEffect;
import com.letraaletra.api.features.user.domain.exception.InvalidUserEffectException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ExperienceBonusEffectTest {

    @Test
    @DisplayName("Deve estar válido quando expiresAt ainda não foi atingido")
    void shouldBeValidBeforeExpiration() {
        ExperienceBonusEffect effect = new ExperienceBonusEffect(50, Instant.now().plusSeconds(3600));

        assertTrue(effect.isValid(Instant.now()));
        assertFalse(effect.isExpired(Instant.now()));
        assertFalse(effect.canRemove());
        assertEquals(50, effect.getBonusPercentage());
    }

    @Test
    @DisplayName("Deve estar expirado quando expiresAt já foi atingido")
    void shouldBeExpiredAfterExpiration() {
        ExperienceBonusEffect effect = new ExperienceBonusEffect(50, Instant.now().minusSeconds(1));

        assertFalse(effect.isValid(Instant.now()));
        assertTrue(effect.isExpired(Instant.now()));
        assertTrue(effect.canRemove());
    }

    @Test
    @DisplayName("Deve derivar a validade diretamente de expiresAt, sem estado redundante")
    void shouldDeriveValidityFromExpiresAt() {
        Instant now = Instant.now();
        ExperienceBonusEffect effect = new ExperienceBonusEffect(50, now.plusSeconds(60));

        assertTrue(effect.isValid(now));
        assertFalse(effect.isValid(now.plusSeconds(61)));
    }

    @Test
    @DisplayName("Deve rejeitar percentual de bônus inválido")
    void shouldRejectInvalidBonusPercentage() {
        assertThrows(InvalidUserEffectException.class,
                () -> new ExperienceBonusEffect(0, Instant.now().plusSeconds(60)));
        assertThrows(InvalidUserEffectException.class,
                () -> new ExperienceBonusEffect(-10, Instant.now().plusSeconds(60)));
    }

    @Test
    @DisplayName("Deve rejeitar expiresAt nulo")
    void shouldRejectNullExpiresAt() {
        assertThrows(NullPointerException.class, () -> new ExperienceBonusEffect(50, null));
    }
}
