package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetSentPendingRequestsInput;
import com.letraaletra.api.features.friend.application.output.GetSentPendingRequestsOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
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

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetSentPendingRequestsUseCase useCase;

    @Test
    @DisplayName("should return sent pending requests for the user")
    void returnsSentRequests() {
        UUID userId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        GetSentPendingRequestsInput input = new GetSentPendingRequestsInput(userId);
        List<Friend> sent = List.of(
                Friend.create(userId, targetId),
                Friend.restore(userId, UUID.randomUUID(), FriendStatus.PENDING, LocalDateTime.now())
        );

        User target = mock(User.class);
        when(target.getUserId()).thenReturn(targetId);
        when(repository.getSentPendingRequests(userId)).thenReturn(sent);
        when(userRepository.findUsersById(anyList())).thenReturn(List.of(target));

        GetSentPendingRequestsOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(sent, output.requests());
        assertEquals(target, output.users().get(targetId));
        verify(repository, times(1)).getSentPendingRequests(userId);
        verify(userRepository, times(1)).findUsersById(anyList());
    }
}
