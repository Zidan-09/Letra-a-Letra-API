package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.KickParticipantActorCommand;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.participant.application.input.KickParticipantInput;
import com.letraaletra.api.features.participant.application.output.KickParticipantOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KickParticipantUseCaseTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActorManager<Game> gameActorManager;

    @InjectMocks
    private KickParticipantUseCase useCase;

    private UUID gameId;
    private UUID targetUserId;
    private KickParticipantInput input;

    @Mock
    private Game mockGame;

    @Mock
    private Actor mockActor;

    @Mock
    private User mockUser;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();
        UUID moderatorId = UUID.randomUUID();
        input = new KickParticipantInput(gameId, targetUserId, moderatorId);
    }

    @Test
    @DisplayName("Should successfully kick participant, process async actor command, and save updated state")
    void shouldKickParticipantSuccessfully() {
        // Arrange
        when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> future = CompletableFuture.completedFuture(mockGame);
        when(mockActor.enqueueCommand(any(KickParticipantActorCommand.class))).thenReturn(future);

        // Act
        KickParticipantOutput output = useCase.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(mockGame, output.game());

        verify(userRepository).find(targetUserId);
        verify(gameActorManager).get(gameId);
        verify(mockActor).enqueueCommand(any(KickParticipantActorCommand.class));
        verify(userRepository).save(mockUser);
        verify(gameRepository).save(mockGame);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when target user does not exist in repository")
    void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        when(userRepository.find(targetUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));

        verify(userRepository).find(targetUserId);
        verifyNoInteractions(gameActorManager);
        verify(userRepository, never()).save(any());
        verify(gameRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should propagate CompletionException directly when actor asynchronous queue command processing execution crashes")
    void shouldPropagateExceptionWhenActorCommandPipelineFails() {
        // Arrange
        when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockUser));
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Actor system mailbox processing interrupted"));
        when(mockActor.enqueueCommand(any(KickParticipantActorCommand.class))).thenReturn(failedFuture);

        // Act & Assert
        assertThrows(CompletionException.class, () -> useCase.execute(input));

        verify(userRepository).find(targetUserId);
        verify(gameActorManager).get(gameId);
        verify(userRepository, never()).save(any());
        verify(gameRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NullPointerException when the input parameter is null")
    void shouldThrowExceptionWhenInputContextIsNull() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> useCase.execute(null));

        verifyNoInteractions(gameActorManager, userRepository, gameRepository);
    }
}