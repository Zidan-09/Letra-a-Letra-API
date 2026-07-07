package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendFriendRequestUseCaseTest {
    @Mock
    private FriendRepository repository;

    @Mock
    private FriendNotifier notifier;

    @InjectMocks
    private SendFriendRequestUseCase useCase;

    private UUID userId;
    private UUID friendId;
    private LocalDateTime now;
    private SendFriendRequestInput input;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        friendId = UUID.randomUUID();
        now = LocalDateTime.now();

        input = new SendFriendRequestInput(userId, friendId);
    }

    @Test
    @DisplayName("Should send a friend request correctly when no previous request exists")
    void sendFriendRequest() {
        when(repository.find(userId, friendId)).thenReturn(Optional.empty());

        SendFriendRequestOutput output = useCase.execute(input);

        assertEquals(FriendStatus.PENDING, output.friend().getStatus());
        verify(repository, times(1)).save(any(Friend.class));
        verify(notifier, times(1)).notifierUser(friendId);
    }

    @Test
    @DisplayName("Should send a friend request correctly when previous request was declined")
    void sendFriendRequest2() {
        Friend previousFriendRequest = new Friend(userId, friendId, FriendStatus.DECLINED, now);
        when(repository.find(userId, friendId)).thenReturn(Optional.of(previousFriendRequest));

        SendFriendRequestOutput output = useCase.execute(input);

        assertEquals(FriendStatus.PENDING, output.friend().getStatus());
        verify(repository, times(1)).save(any(Friend.class));
        verify(notifier, times(1)).notifierUser(friendId);
    }

    @Test
    @DisplayName("Should throw an InvalidFriendRequestException because current status is already pending")
    void throwError() {
        Friend existingPendingRequest = new Friend(userId, friendId, FriendStatus.PENDING, now);
        when(repository.find(userId, friendId)).thenReturn(Optional.of(existingPendingRequest));

        assertThrows(InvalidFriendRequestException.class, () -> useCase.execute(input));

        verify(repository, never()).save(any());
        verify(notifier, never()).notifierUser(any());
    }
}