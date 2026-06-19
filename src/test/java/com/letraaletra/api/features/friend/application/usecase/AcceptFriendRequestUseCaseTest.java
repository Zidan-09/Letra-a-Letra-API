package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.AcceptFriendRequestInput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.AcceptFriendRequestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcceptFriendRequestUseCaseTest {
    @Mock
    private FriendRepository repository;

    @InjectMocks
    private AcceptFriendRequestUseCase useCase;

    private String userId;
    private String friendId;
    private LocalDateTime now;
    private AcceptFriendRequestInput input;

    @BeforeEach
    void setup() {
        userId = "id -1";
        friendId = "id-2";
        now = LocalDateTime.now();
        input = AcceptFriendRequestMapper.toInput(userId, friendId);
    }

    @Test
    @DisplayName("should accept an friend request correctly")
    void accept() {
        Friend friendRequest = new Friend(userId, friendId, FriendStatus.PENDING, now);

        when(repository.find(userId, friendId)).thenReturn(friendRequest);

        ArgumentCaptor<Friend> friendCaptor = ArgumentCaptor.forClass(Friend.class);

        useCase.execute(input);

        verify(repository).save(friendCaptor.capture());

        Friend friendSaved = friendCaptor.getValue();

        assertEquals(FriendStatus.ACCEPT, friendSaved.getStatus());
    }

    @Test
    @DisplayName("should throw an InvalidFriendRequestException because null")
    void throwInvalidFriendRequestExceptionBecauseNull() {
        when(repository.find(userId, friendId)).thenReturn(null);

        assertThrows(InvalidFriendRequestException.class,
                () -> useCase.execute(input)
        );
    }

    @Test
    @DisplayName("should throw an InvalidFriendRequestException because status")
    void throwInvalidFriendRequestExceptionBecauseStatus() {
        Friend friend = new Friend(userId, friendId, FriendStatus.ACCEPT, now);

        when(repository.find(userId, friendId)).thenReturn(friend);

        assertThrows(InvalidFriendRequestException.class,
                () -> useCase.execute(input)
        );
    }
}