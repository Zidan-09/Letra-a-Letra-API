package com.letraaletra.api.features.items.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.items.domain.effect.EffectType;
import com.letraaletra.api.features.items.domain.effect.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.effect.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ItemEffectCodec Unit Tests")
class ItemEffectCodecTest {

    @Test
    @DisplayName("nulo deve codificar e decodificar como nulo")
    void nullShouldEncodeAndDecodeAsNull() {
        assertNull(ItemEffectCodec.encode(null));
        assertNull(ItemEffectCodec.decode(null));
        assertNull(ItemEffectCodec.decode("  "));
    }

    @Test
    @DisplayName("efeito de troca de nome deve fazer round-trip")
    void nicknameChangeShouldRoundTrip() {
        String raw = ItemEffectCodec.encode(new NicknameChangeEffect());

        assertEquals(new NicknameChangeEffect(), ItemEffectCodec.decode(raw));
    }

    @Test
    @DisplayName("efeito percentual deve fazer round-trip")
    void percentageTimedShouldRoundTrip() {
        PercentageTimedEffect effect = new PercentageTimedEffect(EffectType.XP_BOOST_PCT, 50, 60);

        assertEquals(effect, ItemEffectCodec.decode(ItemEffectCodec.encode(effect)));
    }

    @Test
    @DisplayName("payloads invalidos devem falhar ao decodificar")
    void invalidPayloadsShouldFailDecoding() {
        assertThrows(InvalidItemException.class, () -> ItemEffectCodec.decode("{broken"));
        assertThrows(InvalidItemException.class, () -> ItemEffectCodec.decode("PERCENTAGE_TIMED|XP_BOOST_PCT|50"));
        assertThrows(InvalidItemException.class, () -> ItemEffectCodec.decode("PERCENTAGE_TIMED|UNKNOWN|50|60"));
        assertThrows(InvalidItemException.class, () -> ItemEffectCodec.decode("PERCENTAGE_TIMED|XP_BOOST_PCT|abc|60"));
    }
}
