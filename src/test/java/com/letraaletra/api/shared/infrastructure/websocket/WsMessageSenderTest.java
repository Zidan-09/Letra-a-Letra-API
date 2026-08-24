package com.letraaletra.api.shared.infrastructure.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("WsMessageSender - encode com eventId e envio por sessão/usuário")
class WsMessageSenderTest {

    @Mock
    private WsConnectionRegistry registry;

    @Mock
    private WebSocketSession sessionA;

    @Mock
    private WebSocketSession sessionB;

    private WsMessageSender sender;

    private final ObjectMapper json = new ObjectMapper();

    record SamplePayload(String event, String value) {
        static SamplePayload of(String value) {
            return new SamplePayload("SAMPLE_EVENT", value);
        }
    }

    @BeforeEach
    void setUp() {
        sender = new WsMessageSender(registry);

        lenient().when(sessionA.isOpen()).thenReturn(true);
        lenient().when(sessionB.isOpen()).thenReturn(true);
    }

    @SuppressWarnings("unchecked")
    private List<TextMessage> captured(WebSocketSession session) throws IOException {
        ArgumentCaptor<TextMessage> captor = ArgumentCaptor.forClass(TextMessage.class);
        verify(session).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    @DisplayName("sendToSessions codifica uma única vez com eventId e envia às sessões abertas")
    void shouldEncodeOnceAndSendToOpenSessions() throws Exception {
        when(registry.find("a")).thenReturn(sessionA);
        when(registry.find("b")).thenReturn(sessionB);

        sender.sendToSessions(List.of("a", "b"), SamplePayload.of("hello"));

        JsonNode sentA = json.readTree(captured(sessionA).get(0).getPayload());
        JsonNode sentB = json.readTree(captured(sessionB).get(0).getPayload());

        assertEquals("SAMPLE_EVENT", sentA.path("event").asText());
        assertEquals(false, sentA.path("eventId").isMissingNode());
        assertEquals(sentA.toString(), sentB.toString());
    }

    @Test
    @DisplayName("sendToSessions ignora sessões ausentes ou fechadas")
    void shouldSkipMissingOrClosedSessions() throws Exception {
        WebSocketSession closed = mock(WebSocketSession.class);
        when(closed.isOpen()).thenReturn(false);

        when(registry.find("a")).thenReturn(sessionA);
        when(registry.find("closed")).thenReturn(closed);
        when(registry.find("ghost")).thenReturn(null);

        sender.sendToSessions(List.of("a", "closed", "ghost"), SamplePayload.of("x"));

        assertEquals(1, captured(sessionA).size());
        verify(closed, never()).sendMessage(any());
    }

    @Test
    @DisplayName("falha de IO em uma sessão não interrompe as demais")
    void shouldContinueAfterSendFailure() throws Exception {
        doThrow(new IOException("boom")).when(sessionA).sendMessage(any());
        when(registry.find("a")).thenReturn(sessionA);
        when(registry.find("b")).thenReturn(sessionB);

        sender.sendToSessions(List.of("a", "b"), SamplePayload.of("y"));

        assertEquals("y", json.readTree(captured(sessionB).get(0).getPayload()).path("value").asText());
    }

    @Test
    @DisplayName("sendToUser resolve a sessão antes de codificar e silencia quando não há sessão")
    void shouldResolveByUserAndStaySilentWithoutSession() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID unknown = UUID.randomUUID();
        when(registry.findByUserId(userId)).thenReturn(sessionA);
        when(registry.findByUserId(unknown)).thenReturn(null);

        sender.sendToUser(userId, SamplePayload.of("unicast"));
        sender.sendToUser(unknown, SamplePayload.of("nobody"));

        JsonNode sent = json.readTree(captured(sessionA).get(0).getPayload());
        assertEquals("unicast", sent.path("value").asText());
        assertEquals(false, sent.path("eventId").isMissingNode());

        verify(sessionB, never()).sendMessage(any());
    }
}
