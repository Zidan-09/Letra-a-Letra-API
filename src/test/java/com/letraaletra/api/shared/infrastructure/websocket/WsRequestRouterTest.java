package com.letraaletra.api.shared.infrastructure.websocket;

import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.shared.infrastructure.audit.MdcOperationContext;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import com.letraaletra.api.shared.infrastructure.websocket.handlers.RoomRequestHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.event.Level;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
@DisplayName("WsRequestRouter - roteamento e auditoria")
class WsRequestRouterTest {

    interface SampleRequest extends WsRequest {
        String gameId();
    }

    static class SampleWsRequest implements SampleRequest {
        private final String gameId;

        SampleWsRequest(String gameId) {
            this.gameId = gameId;
        }

        @Override
        public String gameId() {
            return gameId;
        }

        @Override
        public String getAudit() {
            return "sample";
        }
    }

    static class SuppressedWsRequest implements WsRequest {
        @Override
        public String getAudit() {
            return "suppressed";
        }

        @Override
        public boolean suppressCommandAudit() {
            return true;
        }
    }

    static class SuppressedHandler implements RoomRequestHandler<SuppressedWsRequest> {
        @Override
        public Class<SuppressedWsRequest> getType() {
            return SuppressedWsRequest.class;
        }

        @Override
        public void handle(SuppressedWsRequest request, WebSocketSession session) {
            throw new IllegalStateException("suppressed failure");
        }
    }

    static class SampleHandler implements RoomRequestHandler<SampleWsRequest> {
        private final boolean fail;

        SampleHandler(boolean fail) {
            this.fail = fail;
        }

        @Override
        public Class<SampleWsRequest> getType() {
            return SampleWsRequest.class;
        }

        @Override
        public void handle(SampleWsRequest request, WebSocketSession session) {
            if (fail) {
                throw new IllegalStateException("room failure");
            }
        }
    }

    @Mock
    private WsUserDirectory userDirectory;

    @Mock
    private AuditService auditService;

    @Mock
    private WsCommandFailureAuditor commandFailureAuditor;

    @Mock
    private WebSocketSession session;

    private final MdcOperationContext operationContext = new MdcOperationContext();

    @BeforeEach
    void setUp() {
        lenient().when(session.getAttributes()).thenReturn(Map.of("userId", UUID.randomUUID().toString()));
        lenient().when(userDirectory.describe(any(UUID.class))).thenReturn(java.util.Optional.empty());
    }

    private WsRequestRouter router(RoomRequestHandler<?> handler) {
        return new WsRequestRouter(
                List.of(handler),
                userDirectory,
                auditService,
                commandFailureAuditor,
                operationContext
        );
    }

    @Test
    @DisplayName("falha SEM gameId também gera evento FAILURE estruturado")
    void failureWithoutGameIdShouldRecordFailureEvent() {
        WsRequestRouter failingRouter = router(new SampleHandler(true));

        assertThrows(IllegalStateException.class,
                () -> failingRouter.dispatch(new SampleWsRequest(null), session));

        verify(auditService).game(eq(Level.WARN), anyString(), any(), any(), any());
        verify(commandFailureAuditor).recordCommandFailure(eq(session), isNull(), any(Throwable.class));
    }

    @Test
    @DisplayName("sucesso sem gameId registra log técnico e não grava FAILURE")
    void successWithoutGameIdShouldNotRecordFailure() {
        router(new SampleHandler(false)).dispatch(new SampleWsRequest(null), session);

        verify(auditService).game(eq(Level.INFO), anyString(), any(), any());
        verifyNoInteractions(commandFailureAuditor);
    }

    @Test
    @DisplayName("sucesso COM gameId audita com o identificador da sala")
    void successWithGameIdShouldAuditRoomScope() {
        router(new SampleHandler(false))
                .dispatch(new SampleWsRequest("game-1"), session);

        verify(auditService).game(eq("game-1"), isNull(), eq(Level.INFO), anyString(), any(), any());
    }

    @Test
    @DisplayName("request com suppressCommandAudit não audita sucesso nem falha, mas repropaga erro")
    void suppressedRequestsSkipAllCommandAudit() {
        WsRequestRouter multiRouter = new WsRequestRouter(
                List.of(new SampleHandler(false), new SuppressedHandler()),
                userDirectory,
                auditService,
                commandFailureAuditor,
                operationContext
        );

        assertThrows(IllegalStateException.class,
                () -> multiRouter.dispatch(new SuppressedWsRequest(), session));

        verifyNoInteractions(auditService, commandFailureAuditor);
    }

    @Test
    @DisplayName("tipo sem handler registrado dispara IllegalArgumentException")
    void unknownTypeShouldThrow() {
        WsRequestRouter singleHandlerRouter = router(new SampleHandler(false));

        assertThrows(IllegalArgumentException.class,
                () -> singleHandlerRouter.dispatch(new UnregisteredRequest(), session));
    }

    static class UnregisteredRequest implements WsRequest {
        @Override
        public String getAudit() {
            return "unregistered";
        }
    }
}
