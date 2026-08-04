package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.BanParticipantActorCommand;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.participant.application.input.BanParticipantInput;
import com.letraaletra.api.features.participant.application.output.BanParticipantOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
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
class BanParticipantUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ActorManager<Game> gameActorManager;

    @InjectMocks
    private BanParticipantUseCase useCase;

    private UUID gameId;
    private UUID targetUserId;
    private BanParticipantInput input;

    @Mock
    private Game mockGame;

    @Mock
    private Actor mockActor;

    @Mock
    private User mockTargetUser;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        targetUserId = UUID.randomUUID();
        UUID moderatorId = UUID.randomUUID();
        input = new BanParticipantInput(gameId, targetUserId, moderatorId);
    }

    @Test
    @DisplayName("Should successfully ban participant, save target and game state, and return output")
    void shouldBanParticipantSuccessfully() {
        // Arrange
        when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockTargetUser));
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> future = CompletableFuture.completedFuture(mockGame);
        when(mockActor.enqueueCommand(any(BanParticipantActorCommand.class))).thenReturn(future);

        // Act
        BanParticipantOutput output = useCase.execute(input);

        // Assert
        assertNotNull(output);
        assertEquals(mockGame, output.game());

        verify(userRepository).find(targetUserId);
        verify(gameActorManager).get(gameId);
        verify(mockActor).enqueueCommand(any(BanParticipantActorCommand.class));
        verify(userRepository).save(mockTargetUser);
        verify(gameRepository).save(mockGame);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException and avoid actor or repository execution when target user does not exist")
    void shouldThrowUserNotFoundExceptionWhenTargetDoesNotExist() {
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
    @DisplayName("Should propagate CompletionException when actor asynchronous command execution fails")
    void shouldPropagateExceptionWhenActorCommandFails() {
        // Arrange
        when(userRepository.find(targetUserId)).thenReturn(Optional.of(mockTargetUser));
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Game> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Actor process failed"));
        when(mockActor.enqueueCommand(any(BanParticipantActorCommand.class))).thenReturn(failedFuture);

        // Act & Assert
        assertThrows(CompletionException.class, () -> useCase.execute(input));

        verify(userRepository).find(targetUserId);
        verify(gameActorManager).get(gameId);
        verify(userRepository, never()).save(any());
        verify(gameRepository, never()).save(any());
    }
}