package com.letraaletra.api.shared.infrastructure.transaction;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionExecutor Unit Tests")
class TransactionExecutorTest {

    private TransactionTemplate inlineTemplate() {
        return new TransactionTemplate() {
            @Override
            public <T> T execute(TransactionCallback<T> action) {
                return action.doInTransaction(new SimpleTransactionStatus());
            }
        };
    }

    @Test
    @DisplayName("Deve executar a ação via TransactionTemplate e retornar o resultado")
    void execute_WhenActionSucceeds_ShouldReturnResult() {
        TransactionExecutor executor = new TransactionExecutor(inlineTemplate());
        AtomicBoolean executed = new AtomicBoolean(false);

        String result = executor.execute(() -> {
            executed.set(true);
            return "ok";
        });

        assertEquals("ok", result);
        assertTrue(executed.get());
    }

    @Test
    @DisplayName("Deve propagar exceção lançada pela ação")
    void execute_WhenActionFails_ShouldPropagateException() {
        TransactionExecutor executor = new TransactionExecutor(inlineTemplate());

        assertThrows(IllegalStateException.class, () -> executor.execute(() -> {
            throw new IllegalStateException("boom");
        }));
    }
}
