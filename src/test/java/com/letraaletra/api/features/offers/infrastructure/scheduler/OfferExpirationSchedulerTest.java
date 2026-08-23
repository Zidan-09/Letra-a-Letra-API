package com.letraaletra.api.features.offers.infrastructure.scheduler;

import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
import com.letraaletra.api.shared.application.port.OperationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OfferExpirationScheduler Unit Tests")
class OfferExpirationSchedulerTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private BusinessAuditRecorder auditRecorder;

    @Mock
    private OperationContext operationContext;

    private OfferExpirationScheduler scheduler;

    @BeforeEach
    void setUp() {
        lenient().when(operationContext.currentOperationId()).thenReturn(Optional.empty());
        lenient().doAnswer(inv -> { ((Runnable) inv.getArgument(2)).run(); return null; }).when(operationContext).runAsOperation(any(UUID.class), any(), any(Runnable.class));
        scheduler = new OfferExpirationScheduler(offerRepository, auditRecorder, operationContext);
    }

    @Test
    @DisplayName("n??o deve registrar evento nem expirar quando nenhuma oferta est?? expirada")
    void shouldDoNothingWhenNoExpiredOffers() {
        when(offerRepository.findActiveExpiredIds()).thenReturn(List.of());

        scheduler.expireOffers();

        verifyNoInteractions(auditRecorder);
        verify(offerRepository, never()).expireOffers();
    }

    @Test
    @DisplayName("deve expirar e registrar um evento por oferta expirada")
    void shouldExpireAndRecordOneEventPerOffer() {
        UUID offer1 = UUID.randomUUID();
        UUID offer2 = UUID.randomUUID();

        when(offerRepository.findActiveExpiredIds()).thenReturn(List.of(offer1, offer2));

        scheduler.expireOffers();

        verify(offerRepository).expireOffers();

        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditRecorder, times(2)).record(captor.capture());

        List<AuditEvent> events = captor.getAllValues();

        assertEquals(AuditEventType.CATALOG_CHANGED, events.get(0).eventType());
        assertEquals(offer1.toString(), events.get(0).resourceId());
        assertEquals(AuditResourceType.OFFER, events.get(0).resourceType());
        assertEquals(AuditActorType.SYSTEM, events.get(0).actor().type());
        assertEquals(AuditSourceType.SCHEDULER, events.get(0).sourceType());
        assertEquals(Boolean.FALSE, events.get(0).delta().get("active"));
        assertEquals(offer2.toString(), events.get(1).resourceId());
    }
}
