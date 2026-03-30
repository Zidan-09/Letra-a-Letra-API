package com.letraaletra.api.application.game.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.participant.Participant;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.presentation.websocket.PlayerGameContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlayerGameContextResolverTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private PlayerGameContextResolver resolver;

    @Test
    @DisplayName("Should return null when user id is null")
    void userIdNull() {
        PlayerGameContext result = resolver.resolve(null);

        assertNull(result);
        verifyNoInteractions(userRepository, gameRepository);
    }

    @Test
    @DisplayName("Should return null when User not found")
    void userNotFound() {
        when(userRepository.find("user-1")).thenReturn(null);

        PlayerGameContext result = resolver.resolve("user-1");

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when User not in Game")
    void userNotInGame() {
        User user = mock(User.class);

        when(userRepository.find("user-1")).thenReturn(user);
        when(user.isInGame()).thenReturn(false);

        PlayerGameContext result = resolver.resolve("user-1");

        assertNull(result);
    }

    @Test
    @DisplayName("Should leave Game and return null when Game not found")
    void leaveGame() {
        User user = mock(User.class);

        when(userRepository.find("user-1")).thenReturn(user);
        when(user.isInGame()).thenReturn(true);
        when(user.getCurrentGameId()).thenReturn("game-1");

        when(gameRepository.find("game-1")).thenReturn(null);

        PlayerGameContext result = resolver.resolve("user-1");

        assertNull(result);

        verify(user).leaveGame();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should return null when Participant not found")
    void participantNotFound() {
        User user = mock(User.class);
        Game game = mock(Game.class);

        when(userRepository.find("user-1")).thenReturn(user);
        when(user.isInGame()).thenReturn(true);
        when(user.getCurrentGameId()).thenReturn("game-1");

        when(gameRepository.find("game-1")).thenReturn(game);
        when(game.getParticipantByUserId("user-1")).thenReturn(null);

        PlayerGameContext result = resolver.resolve("user-1");

        assertNull(result);
    }

    @Test
    @DisplayName("Should return context when all is valid")
    void execute() {
        User user = mock(User.class);
        Game game = mock(Game.class);
        Participant participant = mock(Participant.class);

        when(userRepository.find("user-1")).thenReturn(user);
        when(user.isInGame()).thenReturn(true);
        when(user.getCurrentGameId()).thenReturn("game-1");

        when(gameRepository.find("game-1")).thenReturn(game);
        when(game.getParticipantByUserId("user-1")).thenReturn(participant);

        PlayerGameContext result = resolver.resolve("user-1");

        assertNotNull(result);
        assertEquals(user, result.user());
        assertEquals(game, result.game());
        assertEquals(participant, result.participant());
    }
}