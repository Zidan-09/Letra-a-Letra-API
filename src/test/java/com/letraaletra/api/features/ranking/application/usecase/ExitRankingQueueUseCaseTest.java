package com.letraaletra.api.features.ranking.application.usecase;

import com.letraaletra.api.features.ranking.application.input.ExitRankingQueueInput;
import com.letraaletra.api.features.ranking.domain.repository.RankingRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.QueueChecker;
import com.letraaletra.api.shared.domain.exception.UserIsNotOnQueueException;
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
class ExitRankingQueueUseCaseTest {

    @Mock
    private RankingRepository rankingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QueueChecker queueChecker;

    @InjectMocks
    private ExitRankingQueueUseCase useCase;

    private UUID userId;
    private ExitRankingQueueInput input;
    private User mockUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        input = new ExitRankingQueueInput(userId);
        mockUser = mock(User.class);
    }

    @Test
    @DisplayName("Should successfully remove user from ranking queue when all validation conditions are met")
    void shouldRemoveUserFromRankingQueueSuccessfully() {
        when(queueChecker.checkQueues(userId)).thenReturn(true);
        when(rankingRepository.onQueue(userId)).thenReturn(true);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(true);

        useCase.execute(input);

        verify(rankingRepository, times(1)).remove(userId);
    }

    @Test
    @DisplayName("Should throw UserIsNotOnQueueException when QueueChecker indicates user is not in any queue")
    void shouldThrowExceptionWhenUserIsNotInAnyQueue() {
        when(queueChecker.checkQueues(userId)).thenReturn(false);

        assertThrows(UserIsNotOnQueueException.class, () -> useCase.execute(input));

        verifyNoInteractions(rankingRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw UserIsNotOnQueueException when RankingRepository indicates user is not in the ranking queue")
    void shouldThrowExceptionWhenUserIsNotInRankingQueue() {
        when(queueChecker.checkQueues(userId)).thenReturn(true);
        when(rankingRepository.onQueue(userId)).thenReturn(false);

        assertThrows(UserIsNotOnQueueException.class, () -> useCase.execute(input));

        verify(rankingRepository, never()).remove(any());
        verifyNoInteractions(userRepository);
    }

    @Test
    @DisplayName("Should throw UserIsNotOnQueueException when user details are not found in UserRepository")
    void shouldThrowExceptionWhenUserNotFoundInRepository() {
        when(queueChecker.checkQueues(userId)).thenReturn(true);
        when(rankingRepository.onQueue(userId)).thenReturn(true);
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        assertThrows(UserIsNotOnQueueException.class, () -> useCase.execute(input));

        verify(rankingRepository, never()).remove(any());
    }

    @Test
    @DisplayName("Should throw UserAlreadyInGameException when user is found but is currently marked as already in a game")
    void shouldThrowExceptionWhenUserIsAlreadyInAGame() {
        when(queueChecker.checkQueues(userId)).thenReturn(true);
        when(rankingRepository.onQueue(userId)).thenReturn(true);
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);

        assertThrows(UserAlreadyInGameException.class, () -> useCase.execute(input));

        verify(rankingRepository, never()).remove(any());
    }
}