package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.CreateGameInput;
import com.letraaletra.api.features.game.application.output.CreateGameOutput;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.RoomCodeService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.room.RoomSettings;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGameUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private RoomTimeoutManager roomTimeoutManager;

    @Mock
    private RoomCodeService roomCodeService;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private CreateGameUseCase useCase;

    private UUID userId;
    private CreateGameInput input;
    private String generatedCode;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        RoomSettings settings = mock(RoomSettings.class);
        generatedCode = "ABC123";

        input = new CreateGameInput(
                "Sala de Teste",
                settings,
                "session-123",
                userId
        );
    }

    @Test
    @DisplayName("Deve criar uma partida com sucesso quando o usuário existe")
    void shouldCreateGameSuccessfully() {
        User user = mock(User.class);
        when(user.getUserId()).thenReturn(userId);

        when(userRepository.find(userId)).thenReturn(Optional.of(user));
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(Collections.emptyList());
        when(roomCodeService.generate()).thenReturn(generatedCode);

        CreateGameOutput output = useCase.execute(input);

        assertNotNull(output);

        Game game = output.game();
        assertNotNull(game);
        assertEquals("Sala de Teste", game.getRoomName());
        assertEquals(generatedCode, game.getCode());

        verify(user).enterGame(game.getId());
        verify(userRepository).save(user);
        verify(gameRepository).save(game);

        verify(actorManager).create(game.getId(), game);
        verify(roomTimeoutManager).start(game);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário criador da sala não for encontrado")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> useCase.execute(input)
        );

        verify(roomCodeService, never()).generate();
        verify(inventoryRepository, never()).findItemsByOwner(any());
        verify(userRepository, never()).save(any());
        verify(gameRepository, never()).save(any());
        verify(actorManager, never()).create(any(), any());
        verify(roomTimeoutManager, never()).start(any());
    }
}