package com.letraaletra.api.features.audit.application.usecase;

import com.letraaletra.api.features.admin.domain.exception.PermissionDeniedException;
import com.letraaletra.api.features.admin.domain.permission.PermissionAction;
import com.letraaletra.api.features.admin.domain.permission.PermissionKey;
import com.letraaletra.api.features.audit.application.input.GetAuditEventsInput;
import com.letraaletra.api.features.audit.application.output.GetAuditEventsOutput;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventFilter;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditOutcome;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.domain.repository.FindAuditEvents;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAuditEventsUseCase Unit Tests")
class GetAuditEventsUseCaseTest {

    @Mock
    private FindAuditEvents findAuditEvents;

    @Mock
    private AdminChecker adminChecker;

    private GetAuditEventsUseCase useCase;

    private AuthenticatedUser adminPrincipal;

    @BeforeEach
    void setUp() {
        useCase = new GetAuditEventsUseCase(findAuditEvents, adminChecker);
        adminPrincipal = new AuthenticatedUser(UUID.randomUUID(), "Admin", true, false);
    }

    @Test
    @DisplayName("deve verificar permissão AUDIT/VIEW e repassar filtros combináveis")
    void shouldCheckPermissionAndPassFilters() {
        UUID targetUserId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        when(findAuditEvents.find(any(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(new PageImpl<>(List.of(sampleEvent())));

        GetAuditEventsInput input = new GetAuditEventsInput(
                adminPrincipal,
                targetUserId,
                null,
                AuditEventType.WALLET_CREDITED,
                AuditCategory.ECONOMY,
                AuditOutcome.SUCCESS,
                AuditResourceType.WALLET,
                null,
                Instant.now().minusSeconds(60),
                Instant.now(),
                null,
                null,
                null,
                transactionId,
                0,
                50,
                true
        );

        GetAuditEventsOutput output = useCase.execute(input);

        verify(adminChecker).check(adminPrincipal, PermissionKey.AUDIT, PermissionAction.VIEW);

        ArgumentCaptor<AuditEventFilter> filterCaptor = ArgumentCaptor.forClass(AuditEventFilter.class);
        verify(findAuditEvents).find(filterCaptor.capture(), eq(0), eq(50), eq(true));

        AuditEventFilter filter = filterCaptor.getValue();
        assertEquals(targetUserId, filter.targetUserId());
        assertEquals(AuditEventType.WALLET_CREDITED, filter.eventType());
        assertEquals(AuditCategory.ECONOMY, filter.category());
        assertEquals(transactionId, filter.transactionId());

        assertEquals(1, output.events().getContent().size());
    }

    @Test
    @DisplayName("deve limitar tamanho de página ao máximo permitido")
    void shouldClampPageSize() {
        when(findAuditEvents.find(any(), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(Page.empty());

        GetAuditEventsInput input = new GetAuditEventsInput(
                adminPrincipal, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                -5,
                10_000,
                false
        );

        useCase.execute(input);

        verify(findAuditEvents).find(any(), eq(0), eq(200), eq(false));
    }

    @Test
    @DisplayName("deve negar consulta sem a permissão adequada")
    void shouldDenyWithoutPermission() {
        doThrow(new PermissionDeniedException())
                .when(adminChecker)
                .check(adminPrincipal, PermissionKey.AUDIT, PermissionAction.VIEW);

        GetAuditEventsInput input = new GetAuditEventsInput(
                adminPrincipal, null, null, null, null, null,
                null, null, null, null, null, null, null, null,
                0, 20, false
        );

        assertThrows(PermissionDeniedException.class, () -> useCase.execute(input));

        verifyNoInteractions(findAuditEvents);
    }

    private AuditEvent sampleEvent() {
        return AuditEvent.builder()
                .category(AuditCategory.ECONOMY)
                .eventType(AuditEventType.WALLET_CREDITED)
                .actor(new com.letraaletra.api.features.audit.domain.AuditActor(
                        com.letraaletra.api.features.audit.domain.AuditActorType.USER,
                        UUID.randomUUID(),
                        "player"))
                .resourceType(AuditResourceType.WALLET)
                .resourceId(UUID.randomUUID().toString())
                .sourceType(AuditSourceType.HTTP)
                .build();
    }
}
