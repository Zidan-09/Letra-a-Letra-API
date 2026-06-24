package com.letraaletra.api.features.game.application.service;

import com.letraaletra.api.features.game.application.input.CloseRoomInput;
import com.letraaletra.api.features.game.application.output.CloseRoomOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.domain.RoomCloseReasons;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.ActorManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloseRoomDueToTimeoutServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActorManager<Game> actorManager;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private CloseRoomDueToTimeoutService service;

    private UUID userId1;
    private UUID userId2;

    @BeforeEach
    void setup() {
        userId1 = UUID.randomUUID();
        userId2 = UUID.randomUUID();
    }

    @Test
    void shouldCloseRoomSuccessfullyDueToTimeout() {
        Game game = mock(Game.class);

        User user1 = mock(User.class);
        User user2 = mock(User.class);

        var participant1 = mock(com.letraaletra.api.features.participant.domain.Participant.class);
        var participant2 = mock(com.letraaletra.api.features.participant.domain.Participant.class);

        when(participant1.getUserId()).thenReturn(userId1);
        when(participant2.getUserId()).thenReturn(userId2);

        when(game.getParticipants()).thenReturn(List.of(participant1, participant2));
        when(game.getId()).thenReturn("game-123");

        when(userRepository.find(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.find(userId2)).thenReturn(Optional.of(user2));

        CloseRoomInput input = new CloseRoomInput(game);

        CloseRoomOutput output = service.execute(input);

        assertNotNull(output);
        assertEquals(game, output.game());
        assertEquals("ROOM_CLOSED", output.event());
        assertEquals(RoomCloseReasons.INACTIVITY, output.reason());

        verify(user1).leaveGame();
        verify(user2).leaveGame();

        verify(userRepository).save(user1);
        verify(userRepository).save(user2);

        verify(actorManager).remove("game-123");
        verify(gameRepository).save(game);

        verify(game).setGameStatus(GameStatus.CANCELED);
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Game game = mock(Game.class);

        User user1 = mock(User.class);

        var participant1 = mock(com.letraaletra.api.features.participant.domain.Participant.class);
        var participant2 = mock(com.letraaletra.api.features.participant.domain.Participant.class);

        when(participant1.getUserId()).thenReturn(userId1);
        when(participant2.getUserId()).thenReturn(userId2);

        when(game.getParticipants()).thenReturn(List.of(participant1, participant2));

        when(userRepository.find(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.find(userId2)).thenReturn(Optional.empty());

        CloseRoomInput input = new CloseRoomInput(game);

        assertThrows(
                UserNotFoundException.class,
                () -> service.execute(input)
        );

        verify(user1).leaveGame();
        verify(userRepository).save(user1);

        verify(actorManager, never()).remove(anyString());
        verify(gameRepository, never()).save(any());
    }
}