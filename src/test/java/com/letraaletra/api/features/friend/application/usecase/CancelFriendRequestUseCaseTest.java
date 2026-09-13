package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.CancelFriendRequestInput;
import com.letraaletra.api.features.friend.application.port.FriendNotifier;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.exception.CanNotDeclineTheRequestException;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelFriendRequestUseCaseTest {
    @Mock
    private FriendRepository repository;

    @Mock
    private FriendNotifier notifier;

    @InjectMocks
    private CancelFriendRequestUseCase useCase;

    private UUID senderId;
    private UUID receiverId;
    private LocalDateTime now;
    private CancelFriendRequestInput input;

    @BeforeEach
    void setup() {
        senderId = UUID.randomUUID();
        receiverId = UUID.randomUUID();
        now = LocalDateTime.now();

        input = new CancelFriendRequestInput(senderId, receiverId);
    }

    @Test
    @DisplayName("should cancel a pending request when the sender requests it")
    void cancel() {
        Friend pending = new Friend(senderId, receiverId, FriendStatus.PENDING, now);
        when(repository.find(senderId, receiverId)).thenReturn(Optional.of(pending));

        ArgumentCaptor<Friend> captor = ArgumentCaptor.forClass(Friend.class);

        useCase.execute(input);

        verify(repository, times(1)).save(captor.capture());
        assertEquals(FriendStatus.DECLINED, captor.getValue().getStatus());
        verify(notifier, times(1)).notifyFriendshipCancelled(receiverId);
    }

    @Test
    @DisplayName("should be idempotent when cancelling an already cancelled request")
    void cancelIdempotent() {
        Friend declined = new Friend(senderId, receiverId, FriendStatus.DECLINED, now);
        when(repository.find(senderId, receiverId)).thenReturn(Optional.of(declined));

        assertDoesNotThrow(() -> useCase.execute(input));

        verify(repository, times(1)).save(any(Friend.class));
    }

    @Test
    @DisplayName("should throw InvalidFriendRequestException when there is no request")
    void throwWhenMissing() {
        when(repository.find(senderId, receiverId)).thenReturn(Optional.empty());

        assertThrows(InvalidFriendRequestException.class, () -> useCase.execute(input));

        verify(repository, never()).save(any());
        verify(notifier, never()).notifyFriendshipCancelled(any());
    }

    @Test
    @DisplayName("should throw CanNotDeclineTheRequestException when the receiver tries to cancel")
    void throwWhenReceiverCancels() {
        Friend pending = new Friend(senderId, receiverId, FriendStatus.PENDING, now);
        CancelFriendRequestInput receiverInput = new CancelFriendRequestInput(receiverId, senderId);
        when(repository.find(receiverId, senderId)).thenReturn(Optional.of(pending));

        assertThrows(CanNotDeclineTheRequestException.class, () -> useCase.execute(receiverInput));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("should throw InvalidFriendRequestException when cancelling an accepted friendship")
    void throwWhenAccepted() {
        Friend accepted = new Friend(senderId, receiverId, FriendStatus.ACCEPT, now);
        when(repository.find(senderId, receiverId)).thenReturn(Optional.of(accepted));

        assertThrows(InvalidFriendRequestException.class, () -> useCase.execute(input));

        verify(repository, never()).save(any());
    }
}
