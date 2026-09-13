package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetSentPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetSentPendingRequestsOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSentPendingRequestsUseCaseTest {
    @Mock
    private FriendRepository repository;

    @InjectMocks
    private GetSentPendingRequestsUseCase useCase;

    @Test
    @DisplayName("should return sent pending requests for the user")
    void returnsSentRequests() {
        UUID userId = UUID.randomUUID();
        GetSentPendingRequestsInput input = new GetSentPendingRequestsInput(userId);
        List<Friend> sent = List.of(
                Friend.create(userId, UUID.randomUUID()),
                Friend.restore(userId, UUID.randomUUID(), FriendStatus.PENDING, LocalDateTime.now())
        );
        when(repository.getSentPendingRequests(userId)).thenReturn(sent);

        GetSentPendingRequestsOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(sent, output.requests());
        verify(repository, times(1)).getSentPendingRequests(userId);
    }
}
