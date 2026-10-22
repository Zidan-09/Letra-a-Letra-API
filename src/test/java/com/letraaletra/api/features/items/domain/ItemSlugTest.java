package com.letraaletra.api.features.items.domain;

import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("ItemSlug Unit Tests")
class ItemSlugTest {

    @Test
    @DisplayName("nome com espacos e maiusculas deve virar slug minusculo separado por hifen")
    void nameWithSpacesAndUpperCaseShouldBecomeSlug() {
        assertEquals("letra-a-letra-banner", ItemSlug.from("Letra a Letra Banner").value());
    }

    @ParameterizedTest(name = "{0} deve virar {1}")
    @CsvSource({
            "Avatar, avatar",
            "'Letra a Letra Banner', letra-a-letra-banner",
            "'Mochila Dourada', mochila-dourada",
            "'Avatar 2024', avatar-2024"
    })
    @DisplayName("nomes devem ser normalizados para slug de url")
    void namesShouldBeNormalizedToUrlSlug(String name, String expectedSlug) {
        assertEquals(expectedSlug, ItemSlug.from(name).value());
    }

    @Test
    @DisplayName("espacoes consecutivos devem virar um unico hifen")
    void consecutiveSpacesShouldBecomeSingleHyphen() {
        assertEquals("letra-a-letra", ItemSlug.from("Letra  a   Letra").value());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    @DisplayName("nome vazio deve falhar")
    void blankNameShouldFail(String name) {
        assertThrows(InvalidItemException.class, () -> ItemSlug.from(name));
    }

    @Test
    @DisplayName("slug deve manter o valor original sem espacos e maiusculas")
    void slugShouldExposeNormalizedValue() {
        ItemSlug slug = ItemSlug.from("Letra a Letra Banner");

        assertEquals("letra-a-letra-banner", slug.value());
        assertEquals("letra-a-letra-banner", slug.toString());
    }
}
