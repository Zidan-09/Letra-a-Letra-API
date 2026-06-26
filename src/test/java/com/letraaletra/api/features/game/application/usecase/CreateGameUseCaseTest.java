package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.application.input.CreateGameInput;
import com.letraaletra.api.features.game.application.output.CreateGameOutput;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.application.port.GameTimeoutManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.RoomSettings;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.domain.service.GenerateRoomCode;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.inventory.Inventory;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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
    private GameQueryService gameQueryService;

    @Mock
    private GameTimeoutManager gameTimeoutManager;

    @Mock
    private GenerateRoomCode generateRoomCode;

    @InjectMocks
    private CreateGameUseCase useCase;

    private UUID userId;
    private User user;
    private CreateGameInput input;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();

        user = mock(User.class);
        RoomSettings settings = mock(RoomSettings.class);

        input = new CreateGameInput(
                "Room Test",
                settings,
                "session-123",
                userId
        );
    }

    @Test
    void shouldCreateGameSuccessfully() {
        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.isNotInGame())
                .thenReturn(true);

        Inventory inventory = new Inventory(new ArrayList<>());

        when(user.getInventory())
                .thenReturn(inventory);

        when(generateRoomCode.execute())
                .thenReturn("ABC123");

        when(gameQueryService.existsByCode("ABC123"))
                .thenReturn(false);

        CreateGameOutput output = useCase.execute(input);

        assertNotNull(output);

        Game game = output.game();

        assertNotNull(game);
        assertEquals("Room Test", game.getRoomName());
        assertEquals("ABC123", game.getCode());

        verify(userRepository).save(user);
        verify(gameRepository).save(game);

        verify(actorManager).create(game.getId(), game);
        verify(gameTimeoutManager).start(game);

        verify(user).enterGame(game.getId());
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.find(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> useCase.execute(input)
        );

        verify(gameRepository, never()).save(any());
        verify(actorManager, never()).create(any(), any());
        verify(gameTimeoutManager, never()).start(any());
    }

    @Test
    void shouldThrowExceptionWhenUserIsAlreadyInGame() {
        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.isNotInGame())
                .thenReturn(false);

        assertThrows(
                UserAlreadyInGameException.class,
                () -> useCase.execute(input)
        );

        verify(userRepository, never()).save(any());
        verify(gameRepository, never()).save(any());

        verify(actorManager, never()).create(any(), any());
        verify(gameTimeoutManager, never()).start(any());

        verify(generateRoomCode, never()).execute();
    }

    @Test
    void shouldGenerateNewCodeWhenCodeAlreadyExists() {
        when(userRepository.find(userId))
                .thenReturn(Optional.of(user));

        when(user.isNotInGame())
                .thenReturn(true);

        Inventory inventory = new Inventory(new ArrayList<>());

        when(user.getInventory())
                .thenReturn(inventory);

        when(generateRoomCode.execute())
                .thenReturn("ABC123", "XYZ999");

        when(gameQueryService.existsByCode("ABC123"))
                .thenReturn(true);

        when(gameQueryService.existsByCode("XYZ999"))
                .thenReturn(false);

        CreateGameOutput output = useCase.execute(input);

        assertEquals(
                "XYZ999",
                output.game().getCode()
        );

        verify(generateRoomCode, times(2)).execute();
    }
}