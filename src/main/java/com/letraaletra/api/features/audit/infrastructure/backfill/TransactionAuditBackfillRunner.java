package com.letraaletra.api.features.audit.infrastructure.backfill;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.domain.repository.FindAuditEvents;
import com.letraaletra.api.features.audit.domain.repository.SaveAuditEvent;
import com.letraaletra.api.features.transaction.domain.TransactionDetails;
import com.letraaletra.api.features.transaction.domain.TransactionReason;
import com.letraaletra.api.features.transaction.domain.TransactionsPage;
import com.letraaletra.api.features.transaction.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "audit.backfill.enabled", havingValue = "true")
public class TransactionAuditBackfillRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TransactionAuditBackfillRunner.class);
    private static final String SOURCE_DETAIL = "TRANSACTION_BACKFILL";
    private static final int PAGE_SIZE = 500;

    private final TransactionRepository transactionRepository;
    private final SaveAuditEvent saveAuditEvent;
    private final FindAuditEvents existenceChecker;

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Audit backfill started");

        int page = 0;
        long migrated = 0;
        Page<TransactionDetails> transactions;

        do {
            transactions = transactionRepository.get(new TransactionsPage(page, PAGE_SIZE, org.springframework.data.domain.Sort.by("createdAt").ascending()));

            for (TransactionDetails transaction : transactions) {
                if (existenceChecker.existsByTransactionId(transaction.transactionId())) {                    continue;
                }

                saveAuditEvent.save(toAuditEvent(transaction));
                migrated++;
            }

            page++;
        } while (page < transactions.getTotalPages());

        logger.info("Audit backfill finished: {} events created", migrated);
    }

    private AuditEvent toAuditEvent(TransactionDetails transaction) {
        UUID userId = transaction.userId();

        return AuditEvent.builder()
                .eventId(UUID.randomUUID())
                .occurredAt(transaction.transactionDate().atZone(ZoneId.systemDefault()).toInstant())
                .category(AuditCategory.ECONOMY)
                .eventType(resolveEventType(transaction))
                .actor(resolveActor(transaction))
                .targetUserId(userId)
                .resourceType(AuditResourceType.WALLET)
                .resourceId(userId != null ? userId.toString() : "unknown")
                .beforeState(Map.of(coinKey(transaction), (long) transaction.balanceBefore()))
                .afterState(Map.of(coinKey(transaction), (long) transaction.balanceAfter()))
                .delta(Map.of(coinKey(transaction), resolveSignedAmount(transaction)))
                .reasonCode(transaction.reason() != null ? transaction.reason().name() : null)
                .sourceType(AuditSourceType.SYSTEM)
                .sourceDetail(SOURCE_DETAIL)
                .transactionId(transaction.transactionId())
                .metadata(Map.of("source", "MIGRATION"))
                .build();
    }

    private String coinKey(TransactionDetails transaction) {
        return transaction.coinType() != null ? transaction.coinType().name() : "UNKNOWN";
    }

    private AuditEventType resolveEventType(TransactionDetails transaction) {
        if (transaction.operation() == null) {
            return AuditEventType.WALLET_CREDITED;
        }

        return transaction.operation().name().equals("CREDIT")
                ? AuditEventType.WALLET_CREDITED
                : AuditEventType.WALLET_DEBITED;
    }

    private long resolveSignedAmount(TransactionDetails transaction) {
        boolean credit = transaction.operation() == null
                || transaction.operation().name().equals("CREDIT");

        return credit ? transaction.amount() : -transaction.amount();
    }

    private AuditActor resolveActor(TransactionDetails transaction) {
        TransactionReason reason = transaction.reason();

        if (reason == TransactionReason.ADMIN_GIVE || reason == TransactionReason.ADMIN_REVOKE) {
            return new AuditActor(AuditActorType.ADMIN, transaction.referenceId(), transaction.referenceName());
        }

        if (reason == TransactionReason.LEVEL_UP) {
            return AuditActor.system();
        }

        return new AuditActor(AuditActorType.USER, transaction.userId(), transaction.username());
    }
}
