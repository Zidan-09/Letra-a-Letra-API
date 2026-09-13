package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.FriendNotFoundException;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    private UserRepository userRepository;

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
        when(userRepository.exists(friendId)).thenReturn(true);
        when(repository.find(userId, friendId)).thenReturn(Optional.empty());

        SendFriendRequestOutput output = useCase.execute(input);

        assertEquals(FriendStatus.PENDING, output.friend().getStatus());
        verify(repository, times(1)).save(any(Friend.class));
        verify(notifier, times(1)).notifierUser(friendId);
    }

    @Test
    @DisplayName("Should reopen the same row when previous request was declined")
    void sendFriendRequest2() {
        Friend previousFriendRequest = new Friend(userId, friendId, FriendStatus.DECLINED, now);
        when(userRepository.exists(friendId)).thenReturn(true);
        when(repository.find(userId, friendId)).thenReturn(Optional.of(previousFriendRequest));

        SendFriendRequestOutput output = useCase.execute(input);

        assertEquals(FriendStatus.PENDING, output.friend().getStatus());
        assertEquals(userId, output.friend().getUserId1());
        assertEquals(friendId, output.friend().getUserId2());

        ArgumentCaptor<Friend> captor = ArgumentCaptor.forClass(Friend.class);
        verify(repository, times(1)).save(captor.capture());
        assertEquals(userId, captor.getValue().getUserId1());
        assertEquals(friendId, captor.getValue().getUserId2());
        verify(notifier, times(1)).notifierUser(friendId);
    }

    @Test
    @DisplayName("Should reuse the existing row when resending from the opposite direction")
    void sendFriendRequestInverseDirection() {
        Friend previousFriendRequest = new Friend(friendId, userId, FriendStatus.DECLINED, now);
        SendFriendRequestInput inverseInput = new SendFriendRequestInput(userId, friendId);
        when(userRepository.exists(friendId)).thenReturn(true);
        when(repository.find(userId, friendId)).thenReturn(Optional.of(previousFriendRequest));

        SendFriendRequestOutput output = useCase.execute(inverseInput);

        assertEquals(FriendStatus.PENDING, output.friend().getStatus());

        ArgumentCaptor<Friend> captor = ArgumentCaptor.forClass(Friend.class);
        verify(repository, times(1)).save(captor.capture());
        assertEquals(friendId, captor.getValue().getUserId1());
        assertEquals(userId, captor.getValue().getUserId2());
        verify(notifier, times(1)).notifierUser(friendId);
    }

    @Test
    @DisplayName("Should throw a FriendNotFoundException when the target user does not exist")
    void throwFriendNotFoundWhenTargetDoesNotExist() {
        when(userRepository.exists(friendId)).thenReturn(false);

        assertThrows(FriendNotFoundException.class, () -> useCase.execute(input));

        verify(repository, never()).find(any(), any());
        verify(repository, never()).save(any());
        verify(notifier, never()).notifierUser(any());
    }

    @Test
    @DisplayName("Should throw an InvalidFriendRequestException because current status is already pending")
    void throwError() {
        Friend existingPendingRequest = new Friend(userId, friendId, FriendStatus.PENDING, now);
        when(userRepository.exists(friendId)).thenReturn(true);
        when(repository.find(userId, friendId)).thenReturn(Optional.of(existingPendingRequest));

        assertThrows(InvalidFriendRequestException.class, () -> useCase.execute(input));

        verify(repository, never()).save(any());
        verify(notifier, never()).notifierUser(any());
    }

    @Test
    @DisplayName("Should throw an InvalidFriendRequestException when sending to yourself")
    void throwErrorWhenSelfRequest() {
        SendFriendRequestInput selfInput = new SendFriendRequestInput(userId, userId);

        assertThrows(InvalidFriendRequestException.class, () -> useCase.execute(selfInput));

        verify(repository, never()).save(any());
        verify(notifier, never()).notifierUser(any());
    }

    @Test
    @DisplayName("Should include the target user profile data in the output")
    void sendFriendRequest_ShouldEnrichOutputWithTargetUser() {
        com.letraaletra.api.features.user.domain.User target = mock(com.letraaletra.api.features.user.domain.User.class);
        when(target.getUserId()).thenReturn(friendId);
        when(userRepository.exists(friendId)).thenReturn(true);
        when(repository.find(userId, friendId)).thenReturn(Optional.empty());
        when(userRepository.find(friendId)).thenReturn(Optional.of(target));

        SendFriendRequestOutput output = useCase.execute(input);

        assertEquals(FriendStatus.PENDING, output.friend().getStatus());
        assertEquals(target, output.users().get(friendId));
    }
}
