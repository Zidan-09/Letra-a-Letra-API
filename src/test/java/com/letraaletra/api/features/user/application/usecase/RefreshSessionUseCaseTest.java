package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.RefreshSessionInput;
import com.letraaletra.api.features.user.application.output.RefreshSessionOutput;
import com.letraaletra.api.features.user.application.port.RefreshTokenGenerator;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.ban.exception.UserBannedFromGameException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.session.SessionRevocationReason;
import com.letraaletra.api.features.user.domain.session.UserSession;
import com.letraaletra.api.features.user.domain.session.exception.SessionRevokedException;
import com.letraaletra.api.features.user.domain.session.repository.UserSessionRepository;
import com.letraaletra.api.shared.domain.exception.SessionExpiredException;
import com.letraaletra.api.shared.domain.security.TokenService;
import com.letraaletra.api.shared.domain.security.exceptions.InvalidTokenException;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshSessionUseCase Unit Tests")
class RefreshSessionUseCaseTest {

    @Mock
    private UserSessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenHashService tokenHashService;

    @Mock
    private RefreshTokenGenerator refreshTokenGenerator;

    @Mock
    private TokenService tokenService;

    private RefreshSessionUseCase useCase;

    private UUID userId;
    private UUID tokenVersion;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tokenVersion = UUID.randomUUID();
        user = mock(User.class);
        useCase = new RefreshSessionUseCase(
                sessionRepository,
                userRepository,
                tokenHashService,
                refreshTokenGenerator,
                tokenService,
                7_776_000_000L,
                300_000L
        );
    }

    private void stubActiveUser() {
        when(userRepository.find(userId)).thenReturn(Optional.of(user));
        when(user.isBanned()).thenReturn(false);
    }

    private void stubActiveUserWithTokens() {
        stubActiveUser();
        when(user.getUserId()).thenReturn(userId);
        when(user.getTokenVersion()).thenReturn(tokenVersion);
    }

    private UserSession activeSession(String currentHash) {
        LocalDateTime now = LocalDateTime.now();
        return UserSession.create(userId, currentHash, now.minusMinutes(1), now.plusDays(90));
    }

    @Test
    @DisplayName("Deve rotacionar o token atual e emitir novo par de credenciais")
    void execute_WhenCurrentToken_ShouldRotateAndReturnNewPair() {
        UserSession session = activeSession("hash-a");

        when(tokenHashService.hash("raw-a")).thenReturn("hash-a");
        when(sessionRepository.findByTokenHashForUpdate("hash-a")).thenReturn(Optional.of(session));
        stubActiveUserWithTokens();
        when(refreshTokenGenerator.generate()).thenReturn("raw-b");
        when(tokenHashService.hash("raw-b")).thenReturn("hash-b");
        when(tokenService.generateUserToken(userId, tokenVersion)).thenReturn("access-b");

        RefreshSessionOutput output = useCase.execute(new RefreshSessionInput("raw-a"));

        assertNotNull(output);
        assertEquals(userId, output.id());
        assertEquals("access-b", output.token());
        assertEquals("raw-b", output.refreshToken());
        assertEquals("hash-b", session.getCurrentTokenHash());
        assertEquals("hash-a", session.getPreviousTokenHash());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    @DisplayName("Deve recuperar a rotacao quando o anterior estiver dentro da janela")
    void execute_WhenPreviousWithinWindow_ShouldRotateAgain() {
        LocalDateTime now = LocalDateTime.now();
        UserSession session = UserSession.create(userId, "hash-a", now.minusMinutes(10), now.plusDays(90));
        session.rotate("hash-b", now, now.plusDays(90));

        when(tokenHashService.hash("raw-a")).thenReturn("hash-a");
        when(sessionRepository.findByTokenHashForUpdate("hash-a")).thenReturn(Optional.of(session));
        stubActiveUserWithTokens();
        when(refreshTokenGenerator.generate()).thenReturn("raw-c");
        when(tokenHashService.hash("raw-c")).thenReturn("hash-c");
        when(tokenService.generateUserToken(userId, tokenVersion)).thenReturn("access-c");

        RefreshSessionOutput output = useCase.execute(new RefreshSessionInput("raw-a"));

        assertEquals("raw-c", output.refreshToken());
        assertEquals("hash-c", session.getCurrentTokenHash());
        assertEquals("hash-b", session.getPreviousTokenHash());
        assertFalse(session.matchesCurrent("hash-a"));
        assertFalse(session.matchesPrevious("hash-a"));
    }

    @Test
    @DisplayName("Deve rejeitar token antigo sem vinculo sem afetar a sessao legitima")
    void execute_WhenAncientTokenWithoutLink_ShouldRejectAndPreserveSession() {
        LocalDateTime now = LocalDateTime.now();
        UserSession session = UserSession.create(userId, "hash-a", now.minusMinutes(10), now.plusDays(90));
        session.rotate("hash-b", now, now.plusDays(90));
        session.rotate("hash-c", now.plusMinutes(1), now.plusDays(90));

        when(tokenHashService.hash("raw-a")).thenReturn("hash-a");
        when(sessionRepository.findByTokenHashForUpdate("hash-a")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> useCase.execute(new RefreshSessionInput("raw-a")));

        verify(sessionRepository, never()).save(any(UserSession.class));

        when(tokenHashService.hash("raw-c")).thenReturn("hash-c");
        when(sessionRepository.findByTokenHashForUpdate("hash-c")).thenReturn(Optional.of(session));
        stubActiveUserWithTokens();
        when(refreshTokenGenerator.generate()).thenReturn("raw-d");
        when(tokenHashService.hash("raw-d")).thenReturn("hash-d");
        when(tokenService.generateUserToken(userId, tokenVersion)).thenReturn("access-d");

        RefreshSessionOutput output = useCase.execute(new RefreshSessionInput("raw-c"));

        assertEquals("raw-d", output.refreshToken());
        assertFalse(session.isRevoked());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    @DisplayName("Deve rejeitar o anterior fora da janela de recuperacao")
    void execute_WhenPreviousOutsideWindow_ShouldRevokeAndThrow() {
        LocalDateTime now = LocalDateTime.now();
        UserSession session = UserSession.create(userId, "hash-a", now.minusMinutes(20), now.plusDays(90));
        session.rotate("hash-b", now.minusMinutes(10), now.plusDays(90));

        when(tokenHashService.hash("raw-a")).thenReturn("hash-a");
        when(sessionRepository.findByTokenHashForUpdate("hash-a")).thenReturn(Optional.of(session));
        stubActiveUser();

        assertThrows(InvalidTokenException.class, () -> useCase.execute(new RefreshSessionInput("raw-a")));

        assertTrue(session.isRevoked());
        assertEquals(SessionRevocationReason.TOKEN_REUSE, session.getRevocationReason());
    }

    @Test
    @DisplayName("Deve lancar SessionExpiredException quando a sessao estiver expirada")
    void execute_WhenSessionExpired_ShouldThrow() {
        LocalDateTime now = LocalDateTime.now();
        UserSession session = UserSession.create(userId, "hash-a", now.minusDays(100), now.minusDays(10));

        when(tokenHashService.hash("raw-a")).thenReturn("hash-a");
        when(sessionRepository.findByTokenHashForUpdate("hash-a")).thenReturn(Optional.of(session));

        assertThrows(SessionExpiredException.class, () -> useCase.execute(new RefreshSessionInput("raw-a")));

        verifyNoInteractions(userRepository, refreshTokenGenerator, tokenService);
    }

    @Test
    @DisplayName("Deve lancar SessionRevokedException quando a sessao estiver revogada")
    void execute_WhenSessionRevoked_ShouldThrow() {
        UserSession session = activeSession("hash-a");
        session.revoke(SessionRevocationReason.LOGOUT, LocalDateTime.now());

        when(tokenHashService.hash("raw-a")).thenReturn("hash-a");
        when(sessionRepository.findByTokenHashForUpdate("hash-a")).thenReturn(Optional.of(session));

        assertThrows(SessionRevokedException.class, () -> useCase.execute(new RefreshSessionInput("raw-a")));

        verifyNoInteractions(userRepository, refreshTokenGenerator, tokenService);
    }

    @Test
    @DisplayName("Deve revogar com USER_BANNED quando o usuario estiver banido")
    void execute_WhenUserBanned_ShouldRevokeAndThrow() {
        UserSession session = activeSession("hash-a");

        when(tokenHashService.hash("raw-a")).thenReturn("hash-a");
        when(sessionRepository.findByTokenHashForUpdate("hash-a")).thenReturn(Optional.of(session));
        when(userRepository.find(userId)).thenReturn(Optional.of(user));
        when(user.isBanned()).thenReturn(true);

        assertThrows(UserBannedFromGameException.class, () -> useCase.execute(new RefreshSessionInput("raw-a")));

        assertTrue(session.isRevoked());
        assertEquals(SessionRevocationReason.USER_BANNED, session.getRevocationReason());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    @DisplayName("Deve lancar InvalidTokenException quando o hash nao existir")
    void execute_WhenTokenHashNotFound_ShouldThrow() {
        when(tokenHashService.hash("unknown")).thenReturn("hash-unknown");
        when(sessionRepository.findByTokenHashForUpdate("hash-unknown")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> useCase.execute(new RefreshSessionInput("unknown")));

        verifyNoInteractions(userRepository, refreshTokenGenerator, tokenService);
    }

    @Test
    @DisplayName("Deve lancar InvalidTokenException quando o usuario nao existir mais")
    void execute_WhenUserNotFound_ShouldRevokeAndThrow() {
        UserSession session = activeSession("hash-a");

        when(tokenHashService.hash("raw-a")).thenReturn("hash-a");
        when(sessionRepository.findByTokenHashForUpdate("hash-a")).thenReturn(Optional.of(session));
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> useCase.execute(new RefreshSessionInput("raw-a")));

        assertTrue(session.isRevoked());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    @DisplayName("Deve rejeitar token vazio sem consultar o repositorio")
    void execute_WhenTokenNull_ShouldThrowWithoutSideEffects() {
        when(tokenHashService.hash(null)).thenThrow(new InvalidTokenException());

        assertThrows(InvalidTokenException.class, () -> useCase.execute(new RefreshSessionInput(null)));

        verifyNoInteractions(sessionRepository, userRepository, refreshTokenGenerator, tokenService);
        verify(tokenHashService, times(1)).hash(null);
        verify(tokenHashService, never()).hash(any(String.class));
    }
}
