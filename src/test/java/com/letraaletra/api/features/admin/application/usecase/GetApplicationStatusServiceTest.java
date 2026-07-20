package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;
import com.letraaletra.api.features.admin.application.service.GetApplicationStatusService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetApplicationStatusServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ActorManager<Game> actorManager;

    private GetApplicationStatusService service;

    @BeforeEach
    void setUp() {
        service = new GetApplicationStatusService(
                userRepository,
                sessionRepository,
                actorManager
        );
    }

    @Test
    @DisplayName("Should successfully return application status when user is an authorized admin")
    void shouldReturnStatusWhenUserIsAuthorizedAdmin() {
        long expectedPlayers = 1500L;
        long expectedOnline = 42L;
        long expectedGames = 12L;

        when(userRepository.countUsers()).thenReturn(expectedPlayers);
        when(sessionRepository.playersOnline()).thenReturn(expectedOnline);
        when(actorManager.count()).thenReturn(expectedGames);

        GetApplicationStatusOutput output = service.handle();

        assertNotNull(output, "Output should not be null");
        assertEquals(expectedPlayers, output.users(), "Players count mismatch");
        assertEquals(expectedOnline, output.usersOnline(), "Online players count mismatch");
        assertEquals(expectedGames, output.games(), "Active games count mismatch");

        verify(userRepository, times(1)).countUsers();
        verify(sessionRepository, times(1)).playersOnline();
        verify(actorManager, times(1)).count();
    }

    @Test
    @DisplayName("Should return zero counts when database and cache are completely empty")
    void shouldReturnZeroCountsWhenSystemIsEmpty() {
        when(userRepository.countUsers()).thenReturn(0L);
        when(sessionRepository.playersOnline()).thenReturn(0L);
        when(actorManager.count()).thenReturn(0L);

        GetApplicationStatusOutput output = service.handle();

        assertNotNull(output);
        assertEquals(0L, output.users());
        assertEquals(0L, output.usersOnline());
        assertEquals(0L, output.games());
    }

    @Test
    @DisplayName("Should handle very large limits and high volume system metrics correctly")
    void shouldHandleMaximumValuesCorrectly() {
        long maxPlayers = Long.MAX_VALUE;
        long maxOnline = 5000000L;
        long maxGames = 250000L;

        when(userRepository.countUsers()).thenReturn(maxPlayers);
        when(sessionRepository.playersOnline()).thenReturn(maxOnline);
        when(actorManager.count()).thenReturn(maxGames);

        GetApplicationStatusOutput output = service.handle();

        assertNotNull(output);
        assertEquals(maxPlayers, output.users());
        assertEquals(maxOnline, output.usersOnline());
        assertEquals(maxGames, output.games());
    }

    @Test
    @DisplayName("Should throw RuntimeException when UserRepository fails unexpectedly")
    void shouldThrowExceptionWhenUserRepositoryFails() {

        when(userRepository.countUsers()).thenThrow(new RuntimeException("Database connection timeout"));

        assertThrows(RuntimeException.class, () -> service.handle());

        verify(sessionRepository, never()).playersOnline();
        verify(actorManager, never()).count();
    }

    @Test
    @DisplayName("Should throw RuntimeException when SessionRepository fails unexpectedly")
    void shouldThrowExceptionWhenSessionRepositoryFails() {
        when(userRepository.countUsers()).thenReturn(100L);
        when(sessionRepository.playersOnline()).thenThrow(new RuntimeException("Redis cluster unreachable"));

        assertThrows(RuntimeException.class, () -> service.handle());

        verify(actorManager, never()).count();
    }

    @Test
    @DisplayName("Should throw RuntimeException when ActorManager fails unexpectedly")
    void shouldThrowExceptionWhenActorManagerFails() {
        when(userRepository.countUsers()).thenReturn(100L);
        when(sessionRepository.playersOnline()).thenReturn(10L);
        when(actorManager.count()).thenThrow(new RuntimeException("Actor system failure"));

        assertThrows(RuntimeException.class, () -> service.handle());
    }
}