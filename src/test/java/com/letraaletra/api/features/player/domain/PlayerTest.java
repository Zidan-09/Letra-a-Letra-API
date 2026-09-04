package com.letraaletra.api.features.player.domain;

import com.letraaletra.api.features.player.domain.effect.FreezeEffect;
import com.letraaletra.api.features.player.domain.effect.BlindEffect;
import com.letraaletra.api.features.player.domain.exception.InvalidPlayerActionException;
import com.letraaletra.api.features.game.domain.board.power.PowerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        player = new Player(userId, "player");
    }

    @Test
    @DisplayName("Deve adicionar itens ao inventário até o limite máximo de 5")
    void shouldAddPowersToInventoryUpToLimit() {
        for (int i = 0; i < 5; i++) {
            player.addToInventory(PowerType.FREEZE);
        }
        assertEquals(5, player.getInventory().size());

        player.addToInventory(PowerType.BLIND);
        assertEquals(5, player.getInventory().size());
    }

    @Test
    @DisplayName("Deve remover item do inventário ou lançar exceção caso não exista")
    void shouldRemoveFromInventoryOrThrow() {
        player.addToInventory(PowerType.FREEZE);

        Map<String, PowerType> inventory = player.getInventory();
        String generatedId = inventory.keySet().iterator().next();

        assertDoesNotThrow(() -> player.removeFromInventoryOrThrow(generatedId));
        assertTrue(player.getInventory().isEmpty());

        assertThrows(InvalidPlayerActionException.class, () ->
                player.removeFromInventoryOrThrow("id-inexistente")
        );
    }

    @Test
    @DisplayName("Deve diminuir a duração dos efeitos e removê-los quando expirarem")
    void shouldDecrementEffectDurationAndRemoveWhenExpired() {
        BlindEffect blindEffect = new BlindEffect();
        player.applyEffect(blindEffect);

        assertEquals(1, player.getEffects().size());

        for (int i = 0; i < 5; i++) {
            player.decrementEffectDuration();
        }
        assertEquals(1, player.getEffects().size(), "Ainda deve restar 1 turno de efeito");
        assertEquals(1, player.getEffects().getFirst().getDuration());

        player.decrementEffectDuration();
        assertTrue(player.getEffects().isEmpty(), "O efeito expirado deveria ter sido removido");
    }

    @Test
    @DisplayName("canNotPlay: Deve retornar TRUE se congelado e SEM poderes de fuga")
    void shouldNotBeAbleToPlayWhenFrozenWithoutCounters() {
        player.applyEffect(new FreezeEffect());

        assertTrue(player.canNotPlay(), "Jogador congelado e sem itens de contra-ataque não deveria jogar");
    }

    @Test
    @DisplayName("canNotPlay: Deve retornar FALSE se congelado mas POSSUI UNFREEZE ou IMMUNITY")
    void shouldBeAbleToPlayWhenFrozenButHasCounterPower() {
        player.applyEffect(new FreezeEffect());

        player.addToInventory(PowerType.UNFREEZE);
        assertFalse(player.canNotPlay(), "Deveria conseguir jogar pois possui UNFREEZE");

        Player anotherPlayer = new Player(UUID.randomUUID(), "player");
        anotherPlayer.applyEffect(new FreezeEffect());
        anotherPlayer.addToInventory(PowerType.IMMUNITY);
        assertFalse(anotherPlayer.canNotPlay(), "Deveria conseguir jogar pois possui IMMUNITY");
    }

    @Test
    @DisplayName("isFrozen: Deve retornar TRUE quando FreezeEffect aplicado")
    void shouldBeFrozenWhenFreezeApplied() {
        assertFalse(player.isFrozen());
        player.applyEffect(new FreezeEffect());
        assertTrue(player.isFrozen());
    }

    @Test
    @DisplayName("isFrozen: Deve retornar FALSE após remover FreezeEffect")
    void shouldNotBeFrozenAfterRemove() {
        player.applyEffect(new FreezeEffect());
        assertTrue(player.isFrozen());
        player.removeEffect(FreezeEffect.class);
        assertFalse(player.isFrozen());
    }

    @Test
    @DisplayName("hasFreezeDefense: Deve retornar TRUE quando possui UNFREEZE")
    void shouldHaveDefenseWhenUnfreeze() {
        assertFalse(player.hasFreezeDefense());
        player.addToInventory(PowerType.UNFREEZE);
        assertTrue(player.hasFreezeDefense());
    }

    @Test
    @DisplayName("hasFreezeDefense: Deve retornar TRUE quando possui IMMUNITY")
    void shouldHaveDefenseWhenImmunity() {
        player.addToInventory(PowerType.IMMUNITY);
        assertTrue(player.hasFreezeDefense());
    }

    @Test
    @DisplayName("hasFreezeDefense: Deve retornar FALSE quando inventário vazio ou sem defesa")
    void shouldNotHaveDefenseWhenEmptyOrOther() {
        assertFalse(player.hasFreezeDefense());
        player.addToInventory(PowerType.BLOCK);
        player.addToInventory(PowerType.FREEZE);
        player.addToInventory(PowerType.BLIND);
        assertFalse(player.hasFreezeDefense());
    }

    @Test
    @DisplayName("hasFreezeDefense: Deve retornar FALSE após descartar defesa")
    void shouldNotHaveDefenseAfterDiscard() {
        player.addToInventory(PowerType.UNFREEZE);
        assertTrue(player.hasFreezeDefense());
        String id = player.getInventory().keySet().iterator().next();
        player.removeFromInventoryOrThrow(id);
        assertFalse(player.hasFreezeDefense());
    }

    @Test
    @DisplayName("canNotPlay deve usar isFrozen e hasFreezeDefense")
    void shouldCanNotPlayReflectIsFrozenAndHasDefense() {
        assertFalse(player.canNotPlay());
        player.applyEffect(new FreezeEffect());
        assertTrue(player.canNotPlay());
        player.addToInventory(PowerType.UNFREEZE);
        assertFalse(player.canNotPlay());
        player.removeEffect(FreezeEffect.class);
        assertFalse(player.canNotPlay());
    }
}