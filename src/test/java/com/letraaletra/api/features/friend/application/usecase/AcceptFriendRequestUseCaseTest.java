package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.AcceptFriendRequestInput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.CanNotAcceptTheRequestException;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
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
class AcceptFriendRequestUseCaseTest {
    @Mock
    private FriendRepository repository;

    @InjectMocks
    private AcceptFriendRequestUseCase useCase;

    private UUID senderId;
    private UUID receiverId;
    private LocalDateTime now;
    private AcceptFriendRequestInput input;

    @BeforeEach
    void setup() {
        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
        now = LocalDateTime.now();

        input = new AcceptFriendRequestInput(receiverId, senderId);
    }

    @Test
    @DisplayName("should accept a friend request correctly when user is the receiver")
    void accept() {
        Friend friendRequest = new Friend(senderId, receiverId, FriendStatus.PENDING, now);

        when(repository.find(input.userId(), input.friendId()))
                .thenReturn(Optional.of(friendRequest));

        ArgumentCaptor<Friend> friendCaptor = ArgumentCaptor.forClass(Friend.class);

        useCase.execute(input);

        verify(repository, times(1)).save(friendCaptor.capture());
        Friend friendSaved = friendCaptor.getValue();

        assertEquals(FriendStatus.ACCEPT, friendSaved.getStatus());
    }

    @Test
    @DisplayName("should throw an InvalidFriendRequestException because request does not exist")
    void throwInvalidFriendRequestExceptionBecauseNull() {
        when(repository.find(input.userId(), input.friendId())).thenReturn(Optional.empty());

        assertThrows(InvalidFriendRequestException.class,
                () -> useCase.execute(input)
        );
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("should throw an InvalidFriendRequestException because status is not PENDING")
    void throwInvalidFriendRequestExceptionBecauseStatus() {
        Friend friend = new Friend(senderId, receiverId, FriendStatus.ACCEPT, now);

        when(repository.find(input.userId(), input.friendId()))
                .thenReturn(Optional.of(friend));

        assertThrows(InvalidFriendRequestException.class,
                () -> useCase.execute(input)
        );
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("should throw CanNotAcceptTheRequestException when the user trying to accept is not the receiver")
    void throwCanNotAcceptTheRequestException() {
        Friend request = new Friend(receiverId, senderId, FriendStatus.PENDING, now);

        when(repository.find(input.userId(), input.friendId()))
                .thenReturn(Optional.of(request));

        assertThrows(CanNotAcceptTheRequestException.class,
                () -> useCase.execute(input)
        );
        verify(repository, never()).save(any());
    }
}