package com.letraaletra.api.features.audit.infrastructure.backfill;

import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.domain.repository.FindAuditEvents;
import com.letraaletra.api.features.audit.domain.repository.SaveAuditEvent;
import com.letraaletra.api.features.offers.domain.CoinType;
import com.letraaletra.api.features.transaction.domain.OperationType;
import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionAuditBackfillRunner Unit Tests")
class TransactionAuditBackfillRunnerTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private SaveAuditEvent saveAuditEvent;

    @Mock
    private FindAuditEvents existenceChecker;

    private TransactionAuditBackfillRunner runner;

    @BeforeEach
    void setUp() {
        runner = new TransactionAuditBackfillRunner(
                transactionRepository,
                saveAuditEvent,
                existenceChecker
        );
    }

    @Test
    @DisplayName("deve gerar eventos sintéticos marcados como MIGRATION para transações sem evento")
    void shouldGenerateMigrationEventsForTransactionsWithoutEvents() {
        UUID userId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        TransactionDetails details = new TransactionDetails(
                transactionId,
                userId,
                "player",
                CoinType.SOFT,
                100,
                50,
                150,
                OperationType.CREDIT,
                TransactionReason.ADMIN_GIVE,
                UUID.randomUUID(),
                "ADMIN",
                "AdminUser",
                LocalDateTime.now()
        );

        when(transactionRepository.get(any()))
                .thenReturn(new PageImpl<>(List.of(details), PageRequest.of(0, 500), 1));
        when(existenceChecker.existsByTransactionId(transactionId)).thenReturn(false);

        runner.run();

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(saveAuditEvent).save(captor.capture());

        AuditEvent event = captor.getValue();
        assertEquals(AuditCategory.ECONOMY, event.category());
        assertEquals(AuditEventType.WALLET_CREDITED, event.eventType());
        assertEquals(AuditActorType.ADMIN, event.actor().type());
        assertEquals("MIGRATION", event.metadata().get("source"));
        assertEquals(AuditSourceType.SYSTEM, event.sourceType());
        assertEquals(transactionId, event.transactionId());
        assertEquals(100L, ((Number) event.delta().get("SOFT")).longValue());
    }

    @Test
    @DisplayName("re-execução deve ser idempotente: transação com evento existente não gera duplicata")
    void shouldBeIdempotentWhenReExecuted() {
        UUID existingTransactionId = UUID.randomUUID();

        TransactionDetails details = new TransactionDetails(
                existingTransactionId,
                UUID.randomUUID(),
                "player",
                CoinType.HARD,
                10,
                10,
                20,
                OperationType.CREDIT,
                TransactionReason.DAILY_REWARD,
                null,
                null,
                null,
                LocalDateTime.now()
        );

        when(transactionRepository.get(any()))
                .thenReturn(new PageImpl<>(List.of(details), PageRequest.of(0, 500), 1));
        when(existenceChecker.existsByTransactionId(existingTransactionId)).thenReturn(true);

        runner.run();

        verify(saveAuditEvent, never()).save(any(AuditEvent.class));
    }

    @Test
    @DisplayName("débitos devem virar WALLET_DEBITED com ator USER e delta negativo")
    void shouldMapDebitsToWalletDebitedWithNegativeDelta() {
        UUID transactionId = UUID.randomUUID();

        TransactionDetails details = new TransactionDetails(
                transactionId,
                UUID.randomUUID(),
                "player",
                CoinType.SOFT,
                30,
                130,
                100,
                OperationType.DEBIT,
                TransactionReason.SHOP_PURCHASE,
                null,
                "OFFER",
                "Oferta",
                LocalDateTime.now()
        );

        when(transactionRepository.get(any()))
                .thenReturn(new PageImpl<>(List.of(details), PageRequest.of(0, 500), 1));
        when(existenceChecker.existsByTransactionId(transactionId)).thenReturn(false);

        runner.run();

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(saveAuditEvent).save(captor.capture());

        AuditEvent event = captor.getValue();
        assertEquals(AuditEventType.WALLET_DEBITED, event.eventType());
        assertEquals(-30, ((Number) event.delta().get("SOFT")).intValue());
        assertEquals(AuditResourceType.WALLET, event.resourceType());
    }
}
