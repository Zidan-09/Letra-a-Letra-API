package com.letraaletra.api.shared.infrastructure.websocket;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("AfterCommitRunner - execução de ações após o commit")
class AfterCommitRunnerTest {

    @AfterEach
    void tearDown() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    @DisplayName("sem transação ativa a ação executa imediatamente")
    void shouldRunImmediatelyWhenNoTransactionIsActive() {
        AtomicBoolean executed = new AtomicBoolean(false);

        AfterCommitRunner.run(() -> executed.set(true));

        assertTrue(executed.get(), "ação deve executar imediatamente sem transação");
    }

    @Test
    @DisplayName("com transação ativa a ação só executa no afterCommit")
    void shouldDeferUntilAfterCommitWhenTransactionIsActive() {
        TransactionSynchronizationManager.initSynchronization();
        AtomicBoolean executed = new AtomicBoolean(false);

        AfterCommitRunner.run(() -> executed.set(true));

        assertFalse(executed.get(), "ação não deve executar antes do commit");

        List<TransactionSynchronization> synchronizations =
                TransactionSynchronizationManager.getSynchronizations();
        synchronizations.forEach(TransactionSynchronization::afterCommit);

        assertTrue(executed.get(), "ação deve executar após o commit");
    }

    @Test
    @DisplayName("com transação ativa a ação não executa quando a transação falha")
    void shouldNotRunWhenTransactionRollsBack() {
        TransactionSynchronizationManager.initSynchronization();
        AtomicBoolean executed = new AtomicBoolean(false);

        AfterCommitRunner.run(() -> executed.set(true));
        TransactionSynchronizationManager.clearSynchronization();

        assertFalse(executed.get(), "ação não deve executar se a transação não commitar");
    }
}
