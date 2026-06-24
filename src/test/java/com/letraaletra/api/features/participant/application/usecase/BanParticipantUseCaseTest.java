package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.BanParticipantActorCommand;
import com.letraaletra.api.features.participant.application.input.BanParticipantInput;
import com.letraaletra.api.features.participant.application.output.BanParticipantOutput;
import com.letraaletra.api.features.participant.application.output.ModerationContext;
import com.letraaletra.api.features.participant.application.service.ModerationContextService;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BanParticipantUseCaseTest {

    @Mock private ModerationContextService moderationContextService;
    @Mock private UserRepository userRepository;
    @Mock private ActorManager<Game> gameActorManager;
    @Mock private Actor actor;
    @Mock private Game mockGame;
    @Mock private Participant mockParticipant;
    @Mock private User mockTargetUser;

    @InjectMocks
    private BanParticipantUseCase banParticipantUseCase;

    @Test
    @DisplayName("Deve banir o participante da sala com sucesso e atualizar o estado do usuário alvo")
    void shouldBanParticipantSuccessfully() {
        String token = "room-token";
        UUID targetId = UUID.randomUUID();
        UUID moderatorId = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();

        BanParticipantInput input = new BanParticipantInput(token, targetId, moderatorId);
        ModerationContext context = new ModerationContext(mockGame, mockParticipant);

        when(mockGame.getId()).thenReturn(gameId.toString());
        when(moderationContextService.resolve(token, targetId, moderatorId)).thenReturn(context);
        when(gameActorManager.get(gameId.toString())).thenReturn(actor);
        when(actor.enqueueCommand(any(BanParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(mockGame));
        when(userRepository.find(targetId)).thenReturn(Optional.of(mockTargetUser));

        BanParticipantOutput output = banParticipantUseCase.execute(input);

        assertNotNull(output);
        assertEquals(token, output.token());
        assertEquals(mockGame, output.game());

        verify(mockTargetUser, times(1)).leaveGame();
        verify(userRepository, times(1)).save(mockTargetUser);
    }

    @Test
    @DisplayName("Deve lançar UserNotFoundException se o participante banido não constar no repositório")
    void shouldThrowExceptionWhenBannedUserDoesNotExist() {
        String token = "room-token";
        UUID targetId = UUID.randomUUID();
        BanParticipantInput input = new BanParticipantInput(token, targetId, UUID.randomUUID());
        ModerationContext context = new ModerationContext(mockGame, mockParticipant);

        when(moderationContextService.resolve(any(), any(), any())).thenReturn(context);
        when(gameActorManager.get(any())).thenReturn(actor);
        when(actor.enqueueCommand(any(BanParticipantActorCommand.class)))
                .thenReturn(CompletableFuture.completedFuture(mockGame));
        when(userRepository.find(targetId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> banParticipantUseCase.execute(input));
        verify(userRepository, never()).save(any());
    }
}