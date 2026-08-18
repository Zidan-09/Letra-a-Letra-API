package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetFriendPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetFriendPendingRequestsOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetFriendPendingRequestsUseCaseTest {

    @Mock
    private FriendRepository friendRepository;

    private GetFriendPendingRequestsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetFriendPendingRequestsUseCase(friendRepository);
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
    }
}