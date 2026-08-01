package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.RemoveParticipantActorCommand;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.participant.application.input.RemoveParticipantInput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
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
class RemoveParticipantUseCaseTest {

    @Mock
    private ActorManager<Game> gameActorManager;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RemoveParticipantUseCase useCase;

    private UUID gameId;
    private RemoveParticipantInput input;

    @Mock
    private Actor mockActor;

    @Mock
    private Game mockGame;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        input = new RemoveParticipantInput(gameId, userId);
    }

    @Test
    @DisplayName("Deve enfileirar o comando no Ator e salvar o jogo quando retornado pelo Ator")
    void shouldEnqueueCommandAndSaveGameWhenGameIsPresent() {
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Optional<Game>> future =
                CompletableFuture.completedFuture(Optional.of(mockGame));

        when(mockActor.enqueueCommand(any(RemoveParticipantActorCommand.class)))
                .thenReturn(future);

        Void result = useCase.execute(input);

        assertNull(result);

        verify(gameActorManager).get(gameId);
        verify(mockActor).enqueueCommand(any(RemoveParticipantActorCommand.class));
        verify(gameRepository).save(mockGame);
    }

    @Test
    @DisplayName("Não deve interagir com o repositório se o Ator retornar um Optional vazio")
    void shouldDoNothingWhenActorReturnsEmptyGameOptional() {
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Optional<Game>> future =
                CompletableFuture.completedFuture(Optional.empty());

        when(mockActor.enqueueCommand(any(RemoveParticipantActorCommand.class)))
                .thenReturn(future);

        Void result = useCase.execute(input);

        assertNull(result);

        verify(gameActorManager).get(gameId);
        verify(mockActor).enqueueCommand(any(RemoveParticipantActorCommand.class));
        verifyNoInteractions(gameRepository);
    }

    @Test
    @DisplayName("Deve propagar CompletionException diretamente quando o processamento do comando no Ator falhar")
    void shouldPropagateExceptionWhenActorCommandPipelineFails() {
        when(gameActorManager.get(gameId)).thenReturn(mockActor);

        CompletableFuture<Optional<Game>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Mailbox closed or actor dead"));

        when(mockActor.enqueueCommand(any(RemoveParticipantActorCommand.class)))
                .thenReturn(failedFuture);

        assertThrows(CompletionException.class, () -> useCase.execute(input));

        verifyNoInteractions(gameRepository);
    }
}