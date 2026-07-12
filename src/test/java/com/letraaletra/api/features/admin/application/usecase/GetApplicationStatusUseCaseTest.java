package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.GetApplicationStatusInput;
import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.port.AdminChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetApplicationStatusUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private AdminChecker adminChecker;

    private GetApplicationStatusUseCase useCase;

    private static final UUID auth = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new GetApplicationStatusUseCase(
                userRepository,
                sessionRepository,
                actorManager,
                adminChecker
        );
    }

    @Test
    @DisplayName("Should successfully return application status when user is an authorized admin")
    void shouldReturnStatusWhenUserIsAuthorizedAdmin() {
        GetApplicationStatusInput input = new GetApplicationStatusInput(auth);

        long expectedPlayers = 1500L;
        long expectedOnline = 42L;
        long expectedGames = 12L;

        doNothing().when(adminChecker).check(auth);
        when(userRepository.countUsers()).thenReturn(expectedPlayers);
        when(sessionRepository.playersOnline()).thenReturn(expectedOnline);
        when(actorManager.count()).thenReturn(expectedGames);

        GetApplicationStatusOutput output = useCase.execute(input);

        assertNotNull(output, "Output should not be null");
        assertEquals(expectedPlayers, output.users(), "Players count mismatch");
        assertEquals(expectedOnline, output.usersOnline(), "Online players count mismatch");
        assertEquals(expectedGames, output.games(), "Active games count mismatch");

        verify(adminChecker, times(1)).check(auth);
        verify(userRepository, times(1)).countUsers();
        verify(sessionRepository, times(1)).playersOnline();
        verify(actorManager, times(1)).count();
    }

    @Test
    @DisplayName("Should throw SecurityException and block execution when admin check fails")
    void shouldThrowSecurityExceptionWhenAdminCheckFails() {
        UUID invalidAuth = UUID.randomUUID();

        GetApplicationStatusInput input = new GetApplicationStatusInput(invalidAuth);

        doThrow(new SecurityException("Access denied: Not an administrator"))
                .when(adminChecker).check(invalidAuth);

        assertThrows(SecurityException.class, () -> useCase.execute(input),
                "Should propagate the security exception thrown by adminChecker");

        verify(adminChecker, times(1)).check(invalidAuth);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(sessionRepository);
        verifyNoInteractions(actorManager);
    }

    @Test
    @DisplayName("Should return zero counts when database and cache are completely empty")
    void shouldReturnZeroCountsWhenSystemIsEmpty() {
        GetApplicationStatusInput input = new GetApplicationStatusInput(auth);

        doNothing().when(adminChecker).check(auth);
        when(userRepository.countUsers()).thenReturn(0L);
        when(sessionRepository.playersOnline()).thenReturn(0L);
        when(actorManager.count()).thenReturn(0L);

        GetApplicationStatusOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(0L, output.users());
        assertEquals(0L, output.usersOnline());
        assertEquals(0L, output.games());
    }

    @Test
    @DisplayName("Should handle very large limits and high volume system metrics correctly")
    void shouldHandleMaximumValuesCorrectly() {
        GetApplicationStatusInput input = new GetApplicationStatusInput(auth);
        long maxPlayers = Long.MAX_VALUE;
        long maxOnline = 5000000L;
        long maxGames = 250000L;

        doNothing().when(adminChecker).check(auth);
        when(userRepository.countUsers()).thenReturn(maxPlayers);
        when(sessionRepository.playersOnline()).thenReturn(maxOnline);
        when(actorManager.count()).thenReturn(maxGames);

        GetApplicationStatusOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(maxPlayers, output.users());
        assertEquals(maxOnline, output.usersOnline());
        assertEquals(maxGames, output.games());
    }

    @Test
    @DisplayName("Should throw RuntimeException when UserRepository fails unexpectedly")
    void shouldThrowExceptionWhenUserRepositoryFails() {
        GetApplicationStatusInput input = new GetApplicationStatusInput(auth);

        doNothing().when(adminChecker).check(auth);
        when(userRepository.countUsers()).thenThrow(new RuntimeException("Database connection timeout"));

        assertThrows(RuntimeException.class, () -> useCase.execute(input));

        verify(sessionRepository, never()).playersOnline();
        verify(actorManager, never()).count();
    }

    @Test
    @DisplayName("Should throw RuntimeException when SessionRepository fails unexpectedly")
    void shouldThrowExceptionWhenSessionRepositoryFails() {
        GetApplicationStatusInput input = new GetApplicationStatusInput(auth);

        doNothing().when(adminChecker).check(auth);
        when(userRepository.countUsers()).thenReturn(100L);
        when(sessionRepository.playersOnline()).thenThrow(new RuntimeException("Redis cluster unreachable"));

        assertThrows(RuntimeException.class, () -> useCase.execute(input));

        verify(actorManager, never()).count();
    }

    @Test
    @DisplayName("Should throw RuntimeException when ActorManager fails unexpectedly")
    void shouldThrowExceptionWhenActorManagerFails() {
        GetApplicationStatusInput input = new GetApplicationStatusInput(auth);

        doNothing().when(adminChecker).check(auth);
        when(userRepository.countUsers()).thenReturn(100L);
        when(sessionRepository.playersOnline()).thenReturn(10L);
        when(actorManager.count()).thenThrow(new RuntimeException("Actor system failure"));

        assertThrows(RuntimeException.class, () -> useCase.execute(input));
    }

    @Test
    @DisplayName("Should propagate null credentials to checker if the input auth value itself is null")
    void shouldPropagateNullAuthValueToAdminChecker() {
        GetApplicationStatusInput input = new GetApplicationStatusInput(null);
        doThrow(new IllegalArgumentException("Token cannot be null"))
                .when(adminChecker).check(null);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(input));
        verifyNoInteractions(userRepository, sessionRepository, actorManager);
    }
}