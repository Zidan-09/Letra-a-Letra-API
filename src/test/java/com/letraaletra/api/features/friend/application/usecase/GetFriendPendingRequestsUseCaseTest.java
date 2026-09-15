package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetFriendPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetFriendPendingRequestsOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetFriendPendingRequestsUseCaseTest {

    @Mock
    private FriendRepository friendRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.letraaletra.api.features.user.application.port.UserEquippedItemsProvider equippedItemsProvider;

    private GetFriendPendingRequestsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetFriendPendingRequestsUseCase(friendRepository, userRepository, equippedItemsProvider);
        lenient().when(equippedItemsProvider.equippedFor(any())).thenReturn(java.util.Map.of());
    }

    @Test
    void shouldReturnPendingRequests() {
        UUID userId = UUID.randomUUID();
        GetFriendPendingRequestsInput input =
                new GetFriendPendingRequestsInput(userId);

        Friend friend1 = mock(Friend.class);
        Friend friend2 = mock(Friend.class);

        List<Friend> requests = List.of(friend1, friend2);

        when(friendRepository.getPendingRequests(userId))
                .thenReturn(requests);

        GetFriendPendingRequestsOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(requests, output.requests());

        verify(friendRepository).getPendingRequests(userId);
        verifyNoMoreInteractions(friendRepository);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoPendingRequests() {
        UUID userId = UUID.randomUUID();
        GetFriendPendingRequestsInput input =
                new GetFriendPendingRequestsInput(userId);

        when(friendRepository.getPendingRequests(userId))
                .thenReturn(List.of());

        GetFriendPendingRequestsOutput output = useCase.execute(input);

        assertNotNull(output);
        assertTrue(output.requests().isEmpty());

        verify(friendRepository).getPendingRequests(userId);
        verifyNoMoreInteractions(friendRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldBatchLoadCounterpartsInASingleCall() {
        UUID userId = UUID.randomUUID();
        UUID senderId = UUID.randomUUID();
        GetFriendPendingRequestsInput input =
                new GetFriendPendingRequestsInput(userId);

        Friend request = Friend.restore(senderId, userId, FriendStatus.PENDING, LocalDateTime.now());
        when(friendRepository.getPendingRequests(userId))
                .thenReturn(List.of(request));

        User sender = mock(User.class);
        when(sender.getUserId()).thenReturn(senderId);
        when(userRepository.findUsersById(List.of(senderId)))
                .thenReturn(List.of(sender));

        GetFriendPendingRequestsOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(sender, output.users().get(senderId));
        verify(userRepository, times(1)).findUsersById(List.of(senderId));
    }
}
