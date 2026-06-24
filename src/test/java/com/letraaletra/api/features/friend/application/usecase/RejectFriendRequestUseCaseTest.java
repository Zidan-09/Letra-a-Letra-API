package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.RejectFriendRequestInput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.RejectFriendRequestMapper;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RejectFriendRequestUseCaseTest {
    @Mock
    private FriendRepository repository;

    @InjectMocks
    private RejectFriendRequestUseCase useCase;

    private String userId;
    private String friendId;
    private LocalDateTime now;
    private RejectFriendRequestInput input;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID().toString();
        friendId = UUID.randomUUID().toString();
        now = LocalDateTime.now();
        input = RejectFriendRequestMapper.toInput(userId, friendId);
    }

    @Test
    @DisplayName("should reject an friend request correctly")
    void accept() {
        Friend friendRequest = new Friend(UUID.fromString(userId), UUID.fromString(friendId), FriendStatus.PENDING, now);

        when(repository.find(UUID.fromString(userId), UUID.fromString(friendId))).thenReturn(Optional.of(friendRequest));

        ArgumentCaptor<Friend> friendCaptor = ArgumentCaptor.forClass(Friend.class);

        useCase.execute(input);

        verify(repository).save(friendCaptor.capture());

        Friend friendSaved = friendCaptor.getValue();

        assertEquals(FriendStatus.DECLINED, friendSaved.getStatus());
    }

    @Test
    @DisplayName("should throw an InvalidFriendRequestException because null")
    void throwInvalidFriendRequestExceptionBecauseNull() {
        when(repository.find(UUID.fromString(userId), UUID.fromString(friendId))).thenReturn(Optional.empty());

        assertThrows(InvalidFriendRequestException.class,
                () -> useCase.execute(input)
        );
    }

    @Test
    @DisplayName("should throw an InvalidFriendRequestException because status")
    void throwInvalidFriendRequestExceptionBecauseStatus() {
        Friend friend = new Friend(UUID.fromString(userId), UUID.fromString(friendId), FriendStatus.ACCEPT, now);

        when(repository.find(UUID.fromString(userId), UUID.fromString(friendId))).thenReturn(Optional.of(friend));

        assertThrows(InvalidFriendRequestException.class,
                () -> useCase.execute(input)
        );
    }
}