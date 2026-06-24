package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.RemoveFriendInput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.FriendNotFoundException;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.RemoveFriendMapper;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RemoveFriendUseCaseTest {
    @Mock
    private FriendRepository repository;

    @InjectMocks
    private RemoveFriendUseCase useCase;

    private String userId;
    private String friendId;
    private LocalDateTime now;
    private RemoveFriendInput input;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID().toString();
        friendId = UUID.randomUUID().toString();
        now = LocalDateTime.now();
        input = RemoveFriendMapper.toInput(userId, friendId);
    }

    @Test
    @DisplayName("should remove a friend from friendsList correctly")
    void removeFriend() {
        Friend friend = new Friend(UUID.fromString(userId), UUID.fromString(friendId), FriendStatus.ACCEPT, now);

        when(repository.find(UUID.fromString(userId), UUID.fromString(friendId)))
                .thenReturn(Optional.of(friend));

        ArgumentCaptor<Friend> friendCaptor = ArgumentCaptor.forClass(Friend.class);

        useCase.execute(input);

        verify(repository).save(friendCaptor.capture());

        Friend friendSaved = friendCaptor.getValue();

        assertEquals(FriendStatus.DECLINED, friendSaved.getStatus());
    }

    @Test
    @DisplayName("should throw an FriendNotFoundException because null")
    void throwFriendNotFoundExceptionBecauseNull() {
        when(repository.find(UUID.fromString(userId), UUID.fromString(friendId)))
                .thenReturn(Optional.empty());

        assertThrows(FriendNotFoundException.class,
                () -> useCase.execute(input)
        );
    }

    @Test
    @DisplayName("should throw an FriendNotFoundException because status")
    void throwFriendNotFoundExceptionBecauseStatus() {
        Friend friend = new Friend(UUID.fromString(userId), UUID.fromString(friendId), FriendStatus.DECLINED, now);

        when(repository.find(UUID.fromString(userId), UUID.fromString(friendId)))
                .thenReturn(Optional.of(friend));

        assertThrows(FriendNotFoundException.class,
                () -> useCase.execute(input)
        );
    }
}