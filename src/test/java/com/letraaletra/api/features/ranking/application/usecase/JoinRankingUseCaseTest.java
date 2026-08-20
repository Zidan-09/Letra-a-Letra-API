package com.letraaletra.api.features.ranking.application.usecase;

import com.letraaletra.api.features.queue.domain.QueueType;
import com.letraaletra.api.features.queue.domain.repository.QueueRepository;
import com.letraaletra.api.features.ranking.application.input.JoinRankingInput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.queue.domain.OnlineUser;
import com.letraaletra.api.features.queue.domain.exception.UserAlreadyOnQueueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JoinRankingUseCaseTest {

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JoinRankingUseCase useCase;

    private UUID userId;
    private OnlineUser onlineUser;
    private JoinRankingInput input;
    private User mockUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        onlineUser = mock(OnlineUser.class);
        when(onlineUser.userId()).thenReturn(userId);

        input = new JoinRankingInput(onlineUser);
        mockUser = mock(User.class);
    }

    @Test
    @DisplayName("Should successfully add online user to ranking queue when all business rules are satisfied")
    void shouldAddUserToRankingQueueSuccessfully() {
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(true);
        when(queueRepository.onQueue(userId)).thenReturn(false);

        useCase.execute(input);

        verify(queueRepository, times(1)).add(QueueType.RANKING, onlineUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when the user does not exist in the repository")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));

        verifyNoInteractions(queueRepository);
    }

    @Test
    @DisplayName("Should throw UserAlreadyInGameException when user is found but is already active in a game")
    void shouldThrowExceptionWhenUserIsAlreadyInAGame() {
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);

        assertThrows(UserAlreadyInGameException.class, () -> useCase.execute(input));

        verify(queueRepository, never()).add(any(), any());
    }

    @Test
    @DisplayName("Should throw UserAlreadyOnQueueException when user is not in a game but is already waiting in a queue")
    void shouldThrowExceptionWhenUserIsAlreadyInAQueue() {
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(true);
        when(queueRepository.onQueue(userId)).thenReturn(true);

        assertThrows(UserAlreadyOnQueueException.class, () -> useCase.execute(input));

        verify(queueRepository, never()).add(any(), any());
    }
}