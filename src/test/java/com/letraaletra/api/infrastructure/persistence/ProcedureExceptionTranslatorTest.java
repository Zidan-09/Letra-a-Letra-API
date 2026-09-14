package com.letraaletra.api.infrastructure.persistence;

import com.letraaletra.api.features.inventory.domain.exception.DuplicateUniqueItemException;
import com.letraaletra.api.features.inventory.domain.exception.InapplicableContextException;
import com.letraaletra.api.features.inventory.domain.exception.InsufficientQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.InvalidQuantityException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotAvailableException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotOwnedException;
import com.letraaletra.api.features.inventory.domain.exception.MaxStackExceededException;
import com.letraaletra.api.features.inventory.domain.exception.NonConsumableItemException;
import com.letraaletra.api.features.inventory.domain.exception.NonEquipableItemException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProcedureExceptionTranslator Tests")
class ProcedureExceptionTranslatorTest {

    private RuntimeException translate(String state, String message) {
        SQLException sql = new SQLException(message, state);
        DataAccessException ex = new UncategorizedSQLException("task", "SELECT", sql);
        return ProcedureExceptionTranslator.translate(ex);
    }

    @Test
    @DisplayName("deve traduzir os novos códigos P0015-P0024")
    void shouldTranslateNewItemCodes() {
        Map<String, Class<? extends RuntimeException>> expected = Map.of(
                "P0015", ItemNotFoundException.class,
                "P0016", DuplicateUniqueItemException.class,
                "P0017", ItemNotAvailableException.class,
                "P0018", MaxStackExceededException.class,
                "P0019", InvalidQuantityException.class,
                "P0020", NonConsumableItemException.class,
                "P0021", NonEquipableItemException.class,
                "P0022", InsufficientQuantityException.class,
                "P0023", InapplicableContextException.class,
                "P0024", ItemNotOwnedException.class
        );

        expected.forEach((state, type) ->
                assertInstanceOf(type, translate(state, "ERROR: " + type.getSimpleName()),
                        "state " + state + " deve mapear para " + type.getSimpleName()));
    }

    @Test
    @DisplayName("deve preservar traduções legadas e repassar códigos desconhecidos")
    void shouldPreserveLegacyTranslations() {
        assertInstanceOf(ItemNotFoundException.class,
                translate(null, "ERROR: ItemNotFound"));

        DataAccessException unknown = new UncategorizedSQLException("task", "SELECT",
                new SQLException("ERROR: SomethingElse", "P9999"));
        assertSame(unknown, ProcedureExceptionTranslator.translate(unknown));
    }
}
