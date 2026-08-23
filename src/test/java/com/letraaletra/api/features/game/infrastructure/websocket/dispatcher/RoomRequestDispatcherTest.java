package com.letraaletra.api.features.game.infrastructure.websocket.dispatcher;

import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.AuditService;
import com.letraaletra.api.shared.application.port.BusinessAuditRecorder;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomRequestDispatcher Audit Tests")
class RoomRequestDispatcherTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private BusinessAuditRecorder auditRecorder;

    private final MdcOperationContext operationContext = new MdcOperationContext();

    @Mock
    private WebSocketSession session;

    private RoomRequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = new RoomRequestDispatcher(
                List.of(new SampleRoomHandler()),
                userRepository,
                auditService,
                auditRecorder,
                operationContext
        );

        lenient().when(session.getAttributes()).thenReturn(Map.of("userId", UUID.randomUUID().toString()));
        lenient().when(userRepository.find(any(UUID.class))).thenReturn(java.util.Optional.empty());
    }

    interface SampleRequest extends WsRequest {
        String gameId();

        @Override
        String getAudit();
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

    abstract static class SampleHandlerBase implements RoomRequestHandler<SampleWsRequest> {
    }

    static class SampleRoomHandler extends SampleHandlerBase {
        private final boolean fail;

        SampleRoomHandler() {
            this.fail = false;
        }

        @Override
        public Class<SampleWsRequest> getType() {
            return SampleWsRequest.class;
        }

        @Override
        public void handle(SampleWsRequest request, org.springframework.web.socket.WebSocketSession session) {
            if (fail) {
                throw new IllegalStateException("room failure");
            }
        }
    }

    @Test
    @DisplayName("falha SEM gameId também deve gerar evento FAILURE estruturado (buraco atual fechado)")
    void failureWithoutGameIdShouldRecordFailureEvent() {
        RoomRequestDispatcher failingDispatcher = new RoomRequestDispatcher(
                List.of(new FailingSampleHandler()),
                userRepository,
                auditService,
                auditRecorder,
                operationContext
        );

        SampleWsRequest request = new SampleWsRequest(null);

        assertThrows(IllegalStateException.class,
                () -> failingDispatcher.dispatch(request, session));

        verify(auditService).game(eq(Level.WARN), anyString(), any(), any(), any());

        verify(auditRecorder).recordFailure(any(com.letraaletra.api.features.audit.domain.AuditEvent.class));
    }

    @Test
    @DisplayName("sucesso sem gameId continua no log técnico e não grava FAILURE")
    void successWithoutGameIdShouldNotRecordFailure() {
        dispatcher.dispatch(new SampleWsRequest(null), session);

        verify(auditService).game(eq(Level.INFO), anyString(), any(), any());
        verifyNoInteractions(auditRecorder);
    }

    static class FailingSampleHandler extends SampleHandlerBase {
        @Override
        public Class<SampleWsRequest> getType() {
            return SampleWsRequest.class;
        }

        @Override
        public void handle(SampleWsRequest request, org.springframework.web.socket.WebSocketSession session) {
            throw new IllegalStateException("room failure");
        }
    }
}
