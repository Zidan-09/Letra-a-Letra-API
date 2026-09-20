package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.RevokeSessionInput;
import com.letraaletra.api.features.user.domain.session.SessionRevocationReason;
import com.letraaletra.api.features.user.domain.session.UserSession;
import com.letraaletra.api.features.user.domain.session.repository.UserSessionRepository;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RevokeSessionUseCase Unit Tests")
class RevokeSessionUseCaseTest {

    @Mock
    private UserSessionRepository sessionRepository;

    @InjectMocks
    private RevokeSessionUseCase useCase;

    @Test
    @DisplayName("Deve revogar a sessao com LOGOUT quando existir sessao")
    void execute_WhenSessionExists_ShouldRevokeWithLogout() {
        UUID userId = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(userId, "player", false, false);
        UserSession session = UserSession.create(userId, "hash", LocalDateTime.now(), LocalDateTime.now().plusDays(90));

        when(sessionRepository.findByUserId(userId)).thenReturn(Optional.of(session));

        assertNull(useCase.execute(new RevokeSessionInput(principal)));

        assertTrue(session.isRevoked());
        assertEquals(SessionRevocationReason.LOGOUT, session.getRevocationReason());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    @DisplayName("Deve ser idempotente quando nao existir sessao")
    void execute_WhenNoSession_ShouldDoNothing() {
        UUID userId = UUID.randomUUID();
        AuthenticatedUser principal = new AuthenticatedUser(userId, "player", false, false);

        when(sessionRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertNull(useCase.execute(new RevokeSessionInput(principal)));

        verify(sessionRepository, never()).save(any(UserSession.class));
    }
}
