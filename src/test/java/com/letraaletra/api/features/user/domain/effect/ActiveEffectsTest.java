package com.letraaletra.api.features.user.domain.effect;

import com.letraaletra.api.features.user.domain.effect.effects.ExperienceBonusEffect;
import com.letraaletra.api.features.user.domain.effect.effects.RankProtectionEffect;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActiveEffectsTest {

    @Test
    @DisplayName("Deve iniciar vazio")
    void shouldStartEmpty() {
        ActiveEffects effects = ActiveEffects.create();

        assertTrue(effects.isEmpty());
        assertTrue(effects.getEffects().isEmpty());
    }

    @Test
    @DisplayName("restore deve copiar a lista sem compartilhar referencia")
    void restoreShouldCopyWithoutSharingReference() {
        List<UserEffect> source = new ArrayList<>(List.of(new RankProtectionEffect(2)));

        ActiveEffects effects = ActiveEffects.restore(source);
        source.clear();

        assertFalse(effects.isEmpty());
        assertEquals(2, effects.find(RankProtectionEffect.class).orElseThrow().getRemainingUses());
    }

    @Test
    @DisplayName("Deve adicionar, localizar e verificar efeitos por tipo")
    void shouldAddHasAndFindByType() {
        ActiveEffects effects = ActiveEffects.create();
        ExperienceBonusEffect bonus = new ExperienceBonusEffect(50, Instant.now().plusSeconds(3600));

        effects.add(bonus);

        assertFalse(effects.isEmpty());
        assertTrue(effects.has(ExperienceBonusEffect.class));
        assertFalse(effects.has(RankProtectionEffect.class));
        assertEquals(bonus, effects.find(ExperienceBonusEffect.class).orElseThrow());
        assertTrue(effects.find(RankProtectionEffect.class).isEmpty());
    }

    @Test
    @DisplayName("Deve remover efeitos por tipo")
    void shouldRemoveByType() {
        ActiveEffects effects = ActiveEffects.create();
        effects.add(new ExperienceBonusEffect(50, Instant.now().plusSeconds(3600)));
        effects.add(new RankProtectionEffect(3));

        effects.remove(ExperienceBonusEffect.class);

        assertFalse(effects.has(ExperienceBonusEffect.class));
        assertTrue(effects.has(RankProtectionEffect.class));
    }

    @Test
    @DisplayName("Não deve expor a coleção interna de forma mutável")
    void shouldNotExposeMutableCollection() {
        ActiveEffects effects = ActiveEffects.create();
        effects.add(new RankProtectionEffect(3));

        List<UserEffect> exposed = effects.getEffects();

        assertThrows(UnsupportedOperationException.class, () -> exposed.add(new RankProtectionEffect(1)));
        assertEquals(1, effects.getEffects().size());
    }

    @Test
    @DisplayName("Deve remover preguiçosamente apenas os efeitos com condição de remoção no próximo processamento")
    void shouldLazilyPruneOnlyRemovableEffects() {
        ActiveEffects effects = ActiveEffects.create();
        ExperienceBonusEffect expired = new ExperienceBonusEffect(50, Instant.now().minusSeconds(1));
        ExperienceBonusEffect valid = new ExperienceBonusEffect(50, Instant.now().plusSeconds(3600));
        RankProtectionEffect consumed = new RankProtectionEffect(1);
        consumed.use();
        effects.add(expired);
        effects.add(valid);
        effects.add(consumed);

        effects.updateEffects();

        assertFalse(effects.getEffects().contains(expired));
        assertEquals(valid, effects.find(ExperienceBonusEffect.class).orElseThrow());
        assertFalse(effects.has(RankProtectionEffect.class));
    }
}
