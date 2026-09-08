package com.letraaletra.api.shared.application.usecase;

import com.letraaletra.api.shared.application.port.TransactionalExecutorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionalUseCase Unit Tests")
class TransactionalUseCaseTest {

    static class CommitMeException extends RuntimeException {
    }

    static class RecordingExecutor implements TransactionalExecutorService {
        boolean committed = false;
        boolean executed = false;

        @Override
        public <T> T execute(Supplier<T> action) {
            executed = true;
            try {
                T result = action.get();
                committed = true;
                return result;
            } catch (RuntimeException e) {
                committed = false;
                throw e;
            }
        }
    }

    @Nested
    @DisplayName("Fluxo principal")
    class MainFlows {

        @Test
        @DisplayName("Deve executar o delegate dentro da transação e retornar o resultado")
        void execute_WhenDelegateSucceeds_ShouldRunInsideTransactionAndReturnResult() {
            RecordingExecutor executor = new RecordingExecutor();
            AtomicInteger calls = new AtomicInteger(0);
            TransactionalUseCase<String, String> useCase = new TransactionalUseCase<>(
                    input -> {
                        calls.incrementAndGet();
                        return "done:" + input;
                    },
                    executor
            );

            String result = useCase.execute("in");

            assertEquals("done:in", result);
            assertTrue(executor.executed);
            assertTrue(executor.committed);
            assertEquals(1, calls.get());
        }

        @Test
        @DisplayName("Deve propagar exceção do delegate com rollback quando não houver commit-on")
        void execute_WhenDelegateFailsWithoutCommitOn_ShouldPropagateWithRollback() {
            RecordingExecutor executor = new RecordingExecutor();
            TransactionalUseCase<String, String> useCase = new TransactionalUseCase<>(
                    input -> {
                        throw new IllegalStateException("boom");
                    },
                    executor
            );

            assertThrows(IllegalStateException.class, () -> useCase.execute("in"));
            assertTrue(executor.executed);
            assertFalse(executor.committed);
        }
    }

    @Nested
    @DisplayName("Exceções com commit configurado")
    class CommitOnFlows {

        @Test
        @DisplayName("Deve propagar exceção configurada em commit-on com commit da transação")
        void execute_WhenCommitOnException_ShouldPropagateWithCommit() {
            RecordingExecutor executor = new RecordingExecutor();
            TransactionalUseCase<String, String> useCase = new TransactionalUseCase<>(
                    input -> {
                        throw new CommitMeException();
                    },
                    executor,
                    Set.of(CommitMeException.class)
            );

            assertThrows(CommitMeException.class, () -> useCase.execute("in"));
            assertTrue(executor.executed);
            assertTrue(executor.committed);
        }

        @Test
        @DisplayName("Deve manter rollback para exceções fora do commit-on")
        void execute_WhenOtherExceptionWithCommitOnConfigured_ShouldPropagateWithRollback() {
            RecordingExecutor executor = new RecordingExecutor();
            TransactionalUseCase<String, String> useCase = new TransactionalUseCase<>(
                    input -> {
                        throw new IllegalStateException("boom");
                    },
                    executor,
                    Set.of(CommitMeException.class)
            );

            assertThrows(IllegalStateException.class, () -> useCase.execute("in"));
            assertTrue(executor.executed);
            assertFalse(executor.committed);
        }
    }
}
