package com.letraaletra.api.shared.infrastructure.audit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MdcOperationContext Unit Tests")
class MdcOperationContextTest {

    private final MdcOperationContext context = new MdcOperationContext();

    @AfterEach
    void cleanUp() {
        MDC.clear();
    }

    @Test
    @DisplayName("deve retornar vazio quando não há contexto")
    void shouldReturnEmptyWithoutContext() {
        assertEquals(Optional.empty(), context.currentRequestId());
        assertEquals(Optional.empty(), context.currentOperationId());
        assertEquals(Optional.empty(), context.currentCorrelationId());
    }

    @Test
    @DisplayName("bindRequest/unbindRequest devem definir e limpar requestId e sourceDetail")
    void shouldBindAndUnbindRequest() {
        context.bindRequest("req-1", "HTTP POST /shop/offers");

        assertEquals(Optional.of("req-1"), context.currentRequestId());

        context.unbindRequest();

        assertEquals(Optional.empty(), context.currentRequestId());
        assertNull(MDC.get(MdcOperationContext.SOURCE_DETAIL_KEY));
    }

    @Test
    @DisplayName("runAsOperation deve propagar operationId e restaurar o estado anterior no finally")
    void runAsOperationShouldRestorePreviousValue() {
        UUID outer = UUID.randomUUID();
        UUID inner = UUID.randomUUID();

        context.runAsOperation(outer, null, () ->
                context.runAsOperation(inner, "game-1", () -> {
                    assertEquals(Optional.of(inner), context.currentOperationId());
                    assertEquals(Optional.of("game-1"), context.currentCorrelationId());
                })
        );

        assertEquals(Optional.empty(), context.currentOperationId());
        assertEquals(Optional.empty(), context.currentCorrelationId());

        assertEquals(Optional.empty(), context.currentOperationId());
    }

    @Test
    @DisplayName("runAsOperation deve limpar o contexto mesmo quando a ação lança exceção")
    void runAsOperationShouldClearContextOnException() {
        assertThrows(IllegalStateException.class, () ->
                context.runAsOperation(UUID.randomUUID(), () -> {
                    throw new IllegalStateException("boom");
                }));

        assertEquals(Optional.empty(), context.currentOperationId());
    }

    @Test
    @DisplayName("contexto não deve vazar entre threads")
    void contextShouldNotLeakBetweenThreads() throws Exception {
        context.bindRequest("main-req", "HTTP GET /x");
        context.runAsOperation(UUID.randomUUID(), null, () -> { });

        AtomicReference<Optional<String>> fromOtherThread = new AtomicReference<>();

        Thread other = new Thread(() -> {
            fromOtherThread.set(context.currentRequestId());
            context.runAsOperation(UUID.randomUUID(), null, () -> { });
            assertEquals(Optional.empty(), context.currentOperationId());
        });
        other.start();
        other.join();

        assertEquals(Optional.empty(), fromOtherThread.get());
        assertEquals(Optional.of("main-req"), context.currentRequestId());
    }
}
