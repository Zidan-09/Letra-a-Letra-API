package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.SendFriendRequestInput;
import com.letraaletra.api.features.friend.application.output.SendFriendRequestOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.InvalidFriendRequestException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.SendFriendRequestMapper;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendFriendRequestUseCaseTest {
    @Mock
    private FriendRepository repository;

    @InjectMocks
    private SendFriendRequestUseCase useCase;

    private String userId;
    private String friendId;
    private LocalDateTime now;
    private SendFriendRequestInput input;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID().toString();
        friendId = UUID.randomUUID().toString();
        now = LocalDateTime.now();
        input = SendFriendRequestMapper.toInput(userId, friendId);
    }

    @Test
    @DisplayName("should send a friend request correctly")
    void sendFriendRequest() {
        when(repository.find(UUID.fromString(userId), UUID.fromString(friendId)))
                .thenReturn(Optional.empty());

        SendFriendRequestOutput output = useCase.execute(input);

        assertEquals(FriendStatus.PENDING, output.friend().getStatus());
    }

    @Test
    @DisplayName("should send a friend request correctly")
    void sendFriendRequest2() {
        Friend friend = new Friend(UUID.fromString(userId), UUID.fromString(friendId), FriendStatus.DECLINED, now);

        when(repository.find(UUID.fromString(userId), UUID.fromString(friendId)))
                .thenReturn(Optional.of(friend));

        SendFriendRequestOutput output = useCase.execute(input);

        assertEquals(FriendStatus.PENDING, output.friend().getStatus());
    }

    @Test
    @DisplayName("should throw an InvalidFriendRequestException because status")
    void throwError() {
        Friend friend = new Friend(UUID.fromString(userId), UUID.fromString(friendId), FriendStatus.PENDING, now);

        when(repository.find(UUID.fromString(userId), UUID.fromString(friendId)))
                .thenReturn(Optional.of(friend));

        assertThrows(InvalidFriendRequestException.class,
                () -> useCase.execute(input)
        );
    }
}