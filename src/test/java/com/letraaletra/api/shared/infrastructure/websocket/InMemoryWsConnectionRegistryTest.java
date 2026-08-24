package com.letraaletra.api.shared.infrastructure.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InMemoryWsConnectionRegistry — caracterização do ciclo de vida de sessões")
class InMemoryWsConnectionRegistryTest {

    @Mock
    private WebSocketSession oldSession;

    @Mock
    private WebSocketSession newSession;

    private InMemoryWsConnectionRegistry repository;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        repository = new InMemoryWsConnectionRegistry();

        lenient().when(oldSession.getAttributes()).thenReturn(Map.of("userId", userId.toString()));
        lenient().when(newSession.getAttributes()).thenReturn(Map.of("userId", userId.toString()));
        lenient().when(oldSession.getId()).thenReturn("old-session");
        lenient().when(newSession.getId()).thenReturn("new-session");
        lenient().when(oldSession.isOpen()).thenReturn(true);
        lenient().when(newSession.isOpen()).thenReturn(true);
    }

    private WebSocketSession saveAndCapture(WebSocketSession raw) {
        repository.save(raw);
        return repository.find(raw.getId());
    }

    @Test
    @DisplayName("save registra a sessão decorada e find/findByUserId resolvem")
    void shouldSaveDecoratedSessionAndResolveIt() {
        WebSocketSession stored = saveAndCapture(newSession);

        assertInstanceOf(ConcurrentWebSocketSessionDecorator.class, stored);
        assertEquals(stored, repository.findByUserId(userId));
        assertEquals(1, repository.playersOnline());
    }

    @Test
    @DisplayName("re-login do mesmo usuário fecha e remove a sessão anterior")
    void shouldKickPreviousSessionOnDuplicateLogin() throws Exception {
        saveAndCapture(oldSession);

        repository.save(newSession);

        verify(oldSession, atLeastOnce()).close();
        assertNull(repository.find("old-session"));
        assertEquals("new-session", repository.find("new-session").getId());
        assertEquals(1, repository.playersOnline());
    }

    @Test
    @DisplayName("remove descarta apenas a sessão atual do usuário")
    void shouldRemoveOnlyCurrentMapping() {
        saveAndCapture(newSession);

        repository.remove(repository.find("new-session"));

        assertNull(repository.find("new-session"));
        assertNull(repository.findByUserId(userId));
        assertEquals(0, repository.playersOnline());
    }

    @Test
    @DisplayName("sessão antiga removida não apaga o mapeamento da sessão atual do usuário")
    void shouldKeepCurrentMappingWhenRemovingStaleSession() throws Exception {
        saveAndCapture(oldSession);
        saveAndCapture(newSession);
        WebSocketSession current = repository.find("new-session");

        WebSocketSession staleMock = mock(WebSocketSession.class);
        when(staleMock.getId()).thenReturn("old-session");
        when(staleMock.getAttributes()).thenReturn(Map.of("userId", userId.toString()));

        repository.remove(staleMock);

        assertTrue(current.isOpen());
        assertEquals(current, repository.findByUserId(userId));
        verify(newSession, never()).close();
    }

    @Test
    @DisplayName("sessões sem userId não são suportadas (NPE no lock por usuário, comportamento atual)")
    void shouldFailFastForAnonymousSessions() {
        WebSocketSession anonymous = mock(WebSocketSession.class);
        lenient().when(anonymous.getAttributes()).thenReturn(Map.of());
        lenient().when(anonymous.getId()).thenReturn("anon-1");

        assertThrows(NullPointerException.class, () -> repository.save(anonymous));
    }

    @Test
    @DisplayName("concorrência: logins simultâneos do mesmo usuário deixam exatamente uma sessão ativa")
    void shouldTolerateConcurrentLoginsForSameUser() throws Exception {
        int threads = 8;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);

        for (int i = 0; i < threads; i++) {
            final String id = "race-" + i;
            WebSocketSession race = mock(WebSocketSession.class);
            lenient().when(race.getAttributes()).thenReturn(Map.of("userId", userId.toString()));
            lenient().when(race.getId()).thenReturn(id);
            lenient().when(race.isOpen()).thenReturn(true);

            executor.submit(() -> {
                try {
                    start.await();
                    repository.save(race);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        start.countDown();
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        long active = repository.playersOnline();
        assertEquals(1, active);
        assertTrue(repository.findByUserId(userId).isOpen());
    }
}
