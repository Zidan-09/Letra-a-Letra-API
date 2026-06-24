package com.letraaletra.api.features.player.application.usecase;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.actor.command.DiscardPowerActorCommand;
import com.letraaletra.api.features.player.application.input.DiscardPowerInput;
import com.letraaletra.api.features.player.application.output.DiscardPowerOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.domain.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscardPowerUseCaseTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private ActorManager<Game> gameActorManager;

    @Mock
    private Actor actor;

    @Mock
    private Game mockGame;

    private DiscardPowerUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DiscardPowerUseCase(tokenService, gameActorManager);
    }

    @Test
    @DisplayName("Deve executar o descarte de poder enviando o comando correto para o Ator da partida")
    void shouldExecuteDiscardPowerSuccessfully() {
        String tokenGameId = "token-criptografado-da-sala";
        UUID decryptedGameId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String powerId = "power-uuid";

        DiscardPowerInput input = new DiscardPowerInput(tokenGameId, userId, powerId);

        when(tokenService.getTokenContent(tokenGameId)).thenReturn(decryptedGameId);
        when(gameActorManager.get(decryptedGameId.toString())).thenReturn(actor);

        CompletableFuture<Game> futureResult = CompletableFuture.completedFuture(mockGame);
        when(actor.enqueueCommand(any(DiscardPowerActorCommand.class))).thenReturn(futureResult);

        DiscardPowerOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(mockGame, output.game());

        ArgumentCaptor<DiscardPowerActorCommand> commandCaptor = ArgumentCaptor.forClass(DiscardPowerActorCommand.class);
        verify(actor).enqueueCommand(commandCaptor.capture());
    }
}