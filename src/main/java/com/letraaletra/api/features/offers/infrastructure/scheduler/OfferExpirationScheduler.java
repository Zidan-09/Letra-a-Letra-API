package com.letraaletra.api.features.offers.infrastructure.scheduler;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.port.OperationContext;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OfferExpirationScheduler {
    private static final String SOURCE_DETAIL = "OFFER_EXPIRATION_SCHEDULER";

    private final OfferRepository offerRepository;
    private final BusinessAuditRecorder auditRecorder;
    private final OperationContext operationContext;

    @Scheduled(fixedRate = 60000)
    public void expireOffers() {
        List<UUID> expiredIds = offerRepository.findActiveExpiredIds();

        if (expiredIds.isEmpty()) {
            return;
        }

        operationContext.runAsOperation(UUID.randomUUID(), null, () -> {
            offerRepository.expireOffers();

            expiredIds.forEach(offerId -> auditRecorder.record(AuditEvent.builder()
                    .category(AuditCategory.ADMINISTRATION)
                    .eventType(AuditEventType.CATALOG_CHANGED)
                    .actor(AuditActor.system())
                    .resourceType(AuditResourceType.OFFER)
                    .resourceId(offerId.toString())
                    .reasonCode("EXPIRED")
                    .sourceType(AuditSourceType.SCHEDULER)
                    .sourceDetail(SOURCE_DETAIL)
                    .delta(Map.of("active", false))
                    .build()));
        });
    }
}
