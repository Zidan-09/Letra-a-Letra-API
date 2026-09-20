package com.letraaletra.api.features.user.application.usecase;

import com.letraaletra.api.features.user.application.input.IssueSessionInput;
import com.letraaletra.api.features.user.application.output.IssueSessionOutput;
import com.letraaletra.api.features.user.application.port.RefreshTokenGenerator;
import com.letraaletra.api.features.user.domain.session.UserSession;
import com.letraaletra.api.features.user.domain.session.repository.UserSessionRepository;
import com.letraaletra.api.shared.domain.service.TokenHashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IssueSessionUseCase Unit Tests")
class IssueSessionUseCaseTest {

    @Mock
    private UserSessionRepository sessionRepository;

    @Mock
    private RefreshTokenGenerator refreshTokenGenerator;

    @Mock
    private TokenHashService tokenHashService;

    private IssueSessionUseCase useCase;

    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        useCase = new IssueSessionUseCase(sessionRepository, refreshTokenGenerator, tokenHashService, 7_776_000_000L);
    }

    @Test
    @DisplayName("Deve criar nova sessao quando nao existir sessao anterior")
    void execute_WhenNoExistingSession_ShouldCreateNewSession() {
        when(sessionRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.empty());
        when(refreshTokenGenerator.generate()).thenReturn("raw-refresh-token");
        when(tokenHashService.hash("raw-refresh-token")).thenReturn("hashed-token");

        IssueSessionOutput output = useCase.execute(new IssueSessionInput(userId));

        assertNotNull(output);
        assertEquals("raw-refresh-token", output.refreshToken());

        ArgumentCaptor<UserSession> captor = ArgumentCaptor.forClass(UserSession.class);
        verify(sessionRepository, times(1)).save(captor.capture());

        UserSession saved = captor.getValue();
        assertEquals(userId, saved.getUserId());
        assertEquals("hashed-token", saved.getCurrentTokenHash());
    }

    @Test
    @DisplayName("Deve substituir a sessao existente descartando o historico anterior")
    void execute_WhenSessionExists_ShouldRenewSession() {
        UserSession existing = UserSession.create(userId, "old-hash", java.time.LocalDateTime.now().minusDays(1), java.time.LocalDateTime.now().plusDays(89));
        existing.rotate("older-previous", java.time.LocalDateTime.now().minusDays(1), java.time.LocalDateTime.now().plusDays(89));

        when(sessionRepository.findByUserIdForUpdate(userId)).thenReturn(Optional.of(existing));
        when(refreshTokenGenerator.generate()).thenReturn("new-raw-token");
        when(tokenHashService.hash("new-raw-token")).thenReturn("new-hash");

        IssueSessionOutput output = useCase.execute(new IssueSessionInput(userId));

        assertEquals("new-raw-token", output.refreshToken());
        assertEquals("new-hash", existing.getCurrentTokenHash());

        verify(sessionRepository, times(1)).save(any(UserSession.class));
    }
}
