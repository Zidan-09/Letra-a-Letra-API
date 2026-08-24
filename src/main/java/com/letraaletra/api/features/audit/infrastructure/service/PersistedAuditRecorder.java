package com.letraaletra.api.features.audit.infrastructure.service;

import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditOutcome;
import com.letraaletra.api.features.audit.domain.repository.SaveAuditEvent;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.port.OperationContext;
import com.letraaletra.api.shared.infrastructure.audit.MdcOperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class PersistedAuditRecorder implements BusinessAuditRecorder {

    private static final Logger logger = LoggerFactory.getLogger(PersistedAuditRecorder.class);
    private static final String SOURCE_DETAIL_MDC_KEY = MdcOperationContext.SOURCE_DETAIL_KEY;

    private final SaveAuditEvent saveAuditEvent;
    private final OperationContext operationContext;
    private final TransactionTemplate failureTransactionTemplate;

    public PersistedAuditRecorder(
            SaveAuditEvent saveAuditEvent,
            OperationContext operationContext,
            PlatformTransactionManager transactionManager
    ) {
        this.saveAuditEvent = saveAuditEvent;
        this.operationContext = operationContext;
        this.failureTransactionTemplate = new TransactionTemplate(transactionManager);
        this.failureTransactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    @Override
    public void record(AuditEvent event) {
        saveAuditEvent.save(enrich(event));
    }

    @Override
    public void recordFailure(AuditEvent event) {
        AuditEvent failureEvent = enrich(event.toBuilder().outcome(AuditOutcome.FAILURE).build());

        try {
            failureTransactionTemplate.executeWithoutResult(status -> saveAuditEvent.save(failureEvent));
        } catch (Exception ex) {
            logger.error(
                    "Failed to persist FAILURE audit event {} (resource={}/{})",
                    failureEvent.eventType(),
                    failureEvent.resourceType(),
                    failureEvent.resourceId(),
                    ex
            );
        }
    }

    private AuditEvent enrich(AuditEvent event) {
        String mdcSourceDetail = org.slf4j.MDC.get(SOURCE_DETAIL_MDC_KEY);

        return event.toBuilder()
                .requestId(event.requestId() == null
                        ? operationContext.currentRequestId().orElse(null)
                        : event.requestId())
                .operationId(event.operationId() == null
                        ? operationContext.currentOperationId().orElse(null)
                        : event.operationId())
                .correlationId(event.correlationId() == null
                        ? operationContext.currentCorrelationId().orElse(null)
                        : event.correlationId())
                .sourceDetail(event.sourceDetail() == null ? mdcSourceDetail : event.sourceDetail())
                .build();
    }
}
