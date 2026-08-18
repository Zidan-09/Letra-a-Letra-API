package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.FriendNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveFriendUseCaseTest {
    @Mock
    private FriendRepository repository;

    @InjectMocks
    private RemoveFriendUseCase useCase;

    private UUID userId;
    private UUID friendId;
    private LocalDateTime now;
    private RemoveFriendInput input;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        friendId = UUID.randomUUID();
        now = LocalDateTime.now();

        input = new RemoveFriendInput(userId, friendId);
    }

    @Test
    @DisplayName("should remove a friend from friendsList correctly")
    void removeFriend() {
        Friend friend = new Friend(userId, friendId, FriendStatus.ACCEPT, now);
        when(repository.find(input.userId(), input.friendId())).thenReturn(Optional.of(friend));

        ArgumentCaptor<Friend> friendCaptor = ArgumentCaptor.forClass(Friend.class);

        useCase.execute(input);

        verify(repository, times(1)).save(friendCaptor.capture());
        Friend friendSaved = friendCaptor.getValue();

        assertEquals(FriendStatus.DECLINED, friendSaved.getStatus());
    }

    @Test
    @DisplayName("should throw a FriendNotFoundException when relation does not exist")
    void throwFriendNotFoundExceptionBecauseNull() {
        when(repository.find(input.userId(), input.friendId())).thenReturn(Optional.empty());

        assertThrows(FriendNotFoundException.class,
                () -> useCase.execute(input)
        );
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("should throw an InvalidFriendRequestException when trying to remove someone who is not ACCEPT")
    void throwFriendNotFoundExceptionBecauseStatus() {
        Friend friend = new Friend(userId, friendId, FriendStatus.DECLINED, now);
        when(repository.find(input.userId(), input.friendId())).thenReturn(Optional.of(friend));

        assertThrows(InvalidFriendRequestException.class,
                () -> useCase.execute(input)
        );
        verify(repository, never()).save(any());
    }
}