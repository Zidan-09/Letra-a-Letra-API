package com.letraaletra.api.shared.infrastructure.websocket;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.letraaletra.api.shared.domain.DomainException;
import com.letraaletra.api.shared.domain.MessageCode;
import com.letraaletra.api.shared.infrastructure.listener.ShutdownListener;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.ErrorWsResponse;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MainWebSocketEndpoint - caracterização de lifecycle e erros")
class MainWebSocketEndpointTest {

    private enum TestMessages implements MessageCode {
        ROOM_IS_FULL("room_is_full"),
        TURN_EXPIRED("turn_expired");

        private final String message;

        TestMessages(String message) {
            this.message = message;
        }

        @Override
        public String getCode() {
            return name();
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    @JsonTypeName("TEST_PING")
    record TestPingRequest(@jakarta.validation.constraints.NotBlank String value) implements WsRequest {
        @Override
        public String getAudit() {
            return "test ping";
        }
    }

    private interface SpyListener extends WsLifecycleListener {
    }

    @Mock
    private WsConnectionRegistry connectionRegistry;

    @Mock
    private WsRequestRouter requestRouter;

    @Mock
    private SpyListener lifecycleListener;

    @Mock
    private WsUserDirectory userDirectory;

    @Mock
    private WsMessageSender messageSender;

    @Mock
    private WebSocketSession session;

    private final ShutdownListener shutdownListener = new ShutdownListener();

    private MainWebSocketEndpoint endpoint;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        JsonMapper jsonMapper = JsonMapper.builder()
                .registerSubtypes(new NamedType(TestPingRequest.class, "TEST_PING"))
                .build();

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        endpoint = new MainWebSocketEndpoint(
                connectionRegistry,
                requestRouter,
                List.of(lifecycleListener),
                userDirectory,
                messageSender,
                jsonMapper,
                validator,
                shutdownListener
        );

        lenient().when(session.getAttributes()).thenReturn(Map.of("userId", userId.toString()));
        lenient().when(userDirectory.exists(userId)).thenReturn(false);
        lenient().when(userDirectory.describe(userId)).thenReturn(Optional.empty());
    }

    private TextMessage payload(String json) {
        return new TextMessage(json);
    }

    @Test
    @DisplayName("connect salva a sessão e só depois dispara listeners")
    void shouldSaveThenNotifyListenersOnConnect() {
        endpoint.afterConnectionEstablished(session);

        InOrder order = inOrder(connectionRegistry, lifecycleListener);
        order.verify(connectionRegistry).save(session);
        order.verify(lifecycleListener).onConnected(session);
        verify(lifecycleListener, never()).onDisconnected(any());
    }

    @Test
    @DisplayName("mensagem válida é desserializada, validada e roteada")
    void shouldDispatchValidMessage() throws Exception {
        when(userDirectory.exists(userId)).thenReturn(true);

        endpoint.handleTextMessage(session, payload("{\"type\":\"TEST_PING\",\"value\":\"abc\"}"));

        ArgumentCaptor<WsRequest> captor = ArgumentCaptor.forClass(WsRequest.class);
        verify(requestRouter).dispatch(captor.capture(), eq(session));
        assertInstanceOf(TestPingRequest.class, captor.getValue());
    }

    @Test
    @DisplayName("JSON inválido responde erro genérico quando usuário existe")
    void shouldSendGenericErrorForBrokenJson() throws Exception {
        when(userDirectory.exists(userId)).thenReturn(true);

        endpoint.handleTextMessage(session, payload("{not-json"));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(messageSender).sendToUser(eq(userId), captor.capture());
        ErrorWsResponse error = assertInstanceOf(ErrorWsResponse.class, captor.getValue());
        assertEquals("an unexpected internal server error occurred", error.message());
    }

    @Test
    @DisplayName("violação de validação responde erro genérico sem rotear")
    void shouldSendGenericErrorForValidationFailure() {
        when(userDirectory.exists(userId)).thenReturn(true);

        endpoint.handleTextMessage(session, payload("{\"type\":\"TEST_PING\",\"value\":\"\"}"));

        verify(requestRouter, never()).dispatch(any(), any());
        verify(messageSender).sendToUser(eq(userId), any(ErrorWsResponse.class));
    }

    @Test
    @DisplayName("DomainException expõe a mensagem original no erro")
    void shouldExposeDomainExceptionMessage() throws Exception {
        when(userDirectory.exists(userId)).thenReturn(true);
        doThrow(new DomainException(TestMessages.ROOM_IS_FULL))
                .when(requestRouter).dispatch(any(), any());

        endpoint.handleTextMessage(session, payload("{\"type\":\"TEST_PING\",\"value\":\"abc\"}"));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(messageSender).sendToUser(eq(userId), captor.capture());
        ErrorWsResponse error = assertInstanceOf(ErrorWsResponse.class, captor.getValue());
        assertEquals("room_is_full", error.message());
    }

    @Test
    @DisplayName("CompletionException é desembrulhada antes de montar o erro")
    void shouldUnwrapCompletionException() throws Exception {
        when(userDirectory.exists(userId)).thenReturn(true);
        doThrow(new java.util.concurrent.CompletionException(new DomainException(TestMessages.TURN_EXPIRED)))
                .when(requestRouter).dispatch(any(), any());

        endpoint.handleTextMessage(session, payload("{\"type\":\"TEST_PING\",\"value\":\"abc\"}"));

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(messageSender).sendToUser(eq(userId), captor.capture());
        ErrorWsResponse error = assertInstanceOf(ErrorWsResponse.class, captor.getValue());
        assertEquals("turn_expired", error.message());
    }

    @Test
    @DisplayName("usuário inexistente não recebe mensagem de erro")
    void shouldStaySilentWhenUserIsUnknown() throws Exception {
        endpoint.handleTextMessage(session, payload("{broken"));

        verifyNoInteractions(messageSender);
        verify(requestRouter, never()).dispatch(any(), any());
    }

    @Test
    @DisplayName("atributo userId ausente propaga a falha de resolução (comportamento atual)")
    void shouldPropagateWhenUserIdAttributeMissing() {
        when(session.getAttributes()).thenReturn(Map.of());

        assertThrows(RuntimeException.class,
                () -> endpoint.handleTextMessage(session, payload("{\"type\":\"TEST_PING\"}")));

        verifyNoInteractions(messageSender);
    }

    @Test
    @DisplayName("disconnect remove a sessão e dispara listener de desconexão")
    void shouldRemoveAndNotifyListenersOnClose() {
        endpoint.afterConnectionClosed(session, CloseStatus.NORMAL);

        InOrder order = inOrder(connectionRegistry, lifecycleListener);
        order.verify(connectionRegistry).remove(session);
        order.verify(lifecycleListener).onDisconnected(session);
        verify(lifecycleListener, never()).onConnected(any());
    }

    @Test
    @DisplayName("durante shutdown o close não remove nem dispara desconexão")
    void shouldSkipDisconnectHandlingDuringShutdown() {
        shutdownListener.onClose(new ContextClosedEvent(new AnnotationConfigApplicationContext()));

        endpoint.afterConnectionClosed(session, CloseStatus.GOING_AWAY);

        verifyNoInteractions(connectionRegistry, lifecycleListener);
    }
}
