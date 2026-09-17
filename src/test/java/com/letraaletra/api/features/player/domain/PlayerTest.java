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
        player = Player.create(userId, "player");
    }

    @Test
    @DisplayName("Deve adicionar itens ao inventário até o limite máximo de 5")
    void shouldAddPowersToInventoryUpToLimit() {
        for (int i = 0; i < 5; i++) {
            player.getInventory().addToInventory(PowerType.FREEZE);
        }
        assertEquals(5, player.getInventory().getPowers().size());

        player.getInventory().addToInventory(PowerType.BLIND);
        assertEquals(5, player.getInventory().getPowers().size());
    }

    @Test
    @DisplayName("Deve remover item do inventário ou lançar exceção caso não exista")
    void shouldRemoveFromInventoryOrThrow() {
        player.getInventory().addToInventory(PowerType.FREEZE);

        Map<String, PowerType> inventory = player.getInventory().getPowers();
        String generatedId = inventory.keySet().iterator().next();

        assertDoesNotThrow(() -> player.getInventory().removeFromInventoryOrThrow(generatedId));
        assertTrue(player.getInventory().getPowers().isEmpty());

        assertThrows(InvalidPlayerActionException.class, () ->
                player.getInventory().removeFromInventoryOrThrow("id-inexistente")
        );
    }

    @Test
    @DisplayName("Deve diminuir a duração dos efeitos e removê-los quando expirarem")
    void shouldDecrementEffectDurationAndRemoveWhenExpired() {
        BlindEffect blindEffect = new BlindEffect();
        player.getActiveEffects().applyEffect(blindEffect);

        assertEquals(1, player.getActiveEffects().getEffects().size());

        for (int i = 0; i < 5; i++) {
            player.getActiveEffects().decrementEffectDuration();
        }
        assertEquals(1, player.getActiveEffects().getEffects().size(), "Ainda deve restar 1 turno de efeito");
        assertEquals(1, player.getActiveEffects().getEffects().getFirst().getDuration());

        player.getActiveEffects().decrementEffectDuration();
        assertTrue(player.getActiveEffects().getEffects().isEmpty(), "O efeito expirado deveria ter sido removido");
    }

    @Test
    @DisplayName("canNotPlay: Deve retornar TRUE se congelado e SEM poderes de fuga")
    void shouldNotBeAbleToPlayWhenFrozenWithoutCounters() {
        player.getActiveEffects().applyEffect(new FreezeEffect());

        assertTrue(player.canNotPlay(), "Jogador congelado e sem itens de contra-ataque não deveria jogar");
    }

    @Test
    @DisplayName("canNotPlay: Deve retornar FALSE se congelado mas Possui UNFREEZE ou IMMUNITY")
    void shouldBeAbleToPlayWhenFrozenButHasCounterPower() {
        player.getActiveEffects().applyEffect(new FreezeEffect());

        player.getInventory().addToInventory(PowerType.UNFREEZE);
        assertFalse(player.canNotPlay(), "Deveria conseguir jogar pois possui UNFREEZE");

        Player anotherPlayer = Player.create(UUID.randomUUID(), "player");
        anotherPlayer.getActiveEffects().applyEffect(new FreezeEffect());
        anotherPlayer.getInventory().addToInventory(PowerType.IMMUNITY);
        assertFalse(anotherPlayer.canNotPlay(), "Deveria conseguir jogar pois possui IMMUNITY");
    }

    @Test
    @DisplayName("isFrozen: Deve retornar TRUE quando FreezeEffect aplicado")
    void shouldBeFrozenWhenFreezeApplied() {
        assertFalse(player.getActiveEffects().isFrozen());
        player.getActiveEffects().applyEffect(new FreezeEffect());
        assertTrue(player.getActiveEffects().isFrozen());
    }

    @Test
    @DisplayName("isFrozen: Deve retornar FALSE após remover FreezeEffect")
    void shouldNotBeFrozenAfterRemove() {
        player.getActiveEffects().applyEffect(new FreezeEffect());
        assertTrue(player.getActiveEffects().isFrozen());
        player.getActiveEffects().removeEffect(FreezeEffect.class);
        assertFalse(player.getActiveEffects().isFrozen());
    }

    @Test
    @DisplayName("hasFreezeDefense: Deve retornar TRUE quando possui UNFREEZE")
    void shouldHaveDefenseWhenUnfreeze() {
        assertFalse(player.getInventory().hasFreezeDefense());
        player.getInventory().addToInventory(PowerType.UNFREEZE);
        assertTrue(player.getInventory().hasFreezeDefense());
    }

    @Test
    @DisplayName("hasFreezeDefense: Deve retornar TRUE quando possui IMMUNITY")
    void shouldHaveDefenseWhenImmunity() {
        player.getInventory().addToInventory(PowerType.IMMUNITY);
        assertTrue(player.getInventory().hasFreezeDefense());
    }

    @Test
    @DisplayName("hasFreezeDefense: Deve retornar FALSE quando inventário vazio ou sem defesa")
    void shouldNotHaveDefenseWhenEmptyOrOther() {
        assertFalse(player.getInventory().hasFreezeDefense());
        player.getInventory().addToInventory(PowerType.BLOCK);
        player.getInventory().addToInventory(PowerType.FREEZE);
        player.getInventory().addToInventory(PowerType.BLIND);
        assertFalse(player.getInventory().hasFreezeDefense());
    }

    @Test
    @DisplayName("hasFreezeDefense: Deve retornar FALSE após descartar defesa")
    void shouldNotHaveDefenseAfterDiscard() {
        player.getInventory().addToInventory(PowerType.UNFREEZE);
        assertTrue(player.getInventory().hasFreezeDefense());
        String id = player.getInventory().getPowers().keySet().iterator().next();
        player.getInventory().removeFromInventoryOrThrow(id);
        assertFalse(player.getInventory().hasFreezeDefense());
    }

    @Test
    @DisplayName("canNotPlay deve usar isFrozen e hasFreezeDefense")
    void shouldCanNotPlayReflectIsFrozenAndHasDefense() {
        assertFalse(player.canNotPlay());
        player.getActiveEffects().applyEffect(new FreezeEffect());
        assertTrue(player.canNotPlay());
        player.getInventory().addToInventory(PowerType.UNFREEZE);
        assertFalse(player.canNotPlay());
        player.getActiveEffects().removeEffect(FreezeEffect.class);
        assertFalse(player.canNotPlay());
    }
}