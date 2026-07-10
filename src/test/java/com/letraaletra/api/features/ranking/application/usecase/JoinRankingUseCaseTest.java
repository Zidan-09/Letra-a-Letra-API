package com.letraaletra.api.features.ranking.application.usecase;

import com.letraaletra.api.features.ranking.application.input.JoinRankingInput;
import com.letraaletra.api.features.ranking.domain.repository.RankingRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.QueueChecker;
import com.letraaletra.api.shared.domain.OnlineUser;
import com.letraaletra.api.shared.domain.exception.UserAlreadyOnQueueException;
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
    private RankingRepository rankingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QueueChecker queueChecker;

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
        when(queueChecker.checkQueues(userId)).thenReturn(false);

        useCase.execute(input);

        verify(rankingRepository, times(1)).add(onlineUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when the user does not exist in the repository")
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.find(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.execute(input));

        verifyNoInteractions(queueChecker);
        verifyNoInteractions(rankingRepository);
    }

    @Test
    @DisplayName("Should throw UserAlreadyInGameException when user is found but is already active in a game")
    void shouldThrowExceptionWhenUserIsAlreadyInAGame() {
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(false);

        assertThrows(UserAlreadyInGameException.class, () -> useCase.execute(input));

        verifyNoInteractions(queueChecker);
        verify(rankingRepository, never()).add(any());
    }

    @Test
    @DisplayName("Should throw UserAlreadyOnQueueException when user is not in a game but is already waiting in a queue")
    void shouldThrowExceptionWhenUserIsAlreadyInAQueue() {
        when(userRepository.find(userId)).thenReturn(Optional.of(mockUser));
        when(mockUser.isNotInGame()).thenReturn(true);
        when(queueChecker.checkQueues(userId)).thenReturn(true);

        assertThrows(UserAlreadyOnQueueException.class, () -> useCase.execute(input));

        verify(rankingRepository, never()).add(any());
    }
}