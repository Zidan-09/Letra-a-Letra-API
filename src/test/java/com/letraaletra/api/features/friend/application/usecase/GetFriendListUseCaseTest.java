package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetFriendListInput;
import com.letraaletra.api.features.friend.application.output.GetFriendListOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendsPage;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetFriendListUseCaseTest {
    @Mock
    private FriendRepository repository;

    @InjectMocks
    private GetFriendListUseCase useCase;

    @Captor
    private ArgumentCaptor<FriendsPage> pageCaptor;

    private UUID userId;
    private GetFriendListInput input;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID();
        input = new GetFriendListInput(userId, 1, 10, Sort.by("requestDate"));
    }

    @Test
    @DisplayName("should get friends page correctly")
    void getFriends() {
        Page<Friend> page = new PageImpl<>(List.of());
        when(repository.getFriends(eq(userId), any(FriendsPage.class)))
                .thenReturn(page);

        GetFriendListOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(page, output.friends());

        verify(repository).getFriends(eq(userId), pageCaptor.capture());
        assertEquals(1, pageCaptor.getValue().page());
        assertEquals(10, pageCaptor.getValue().size());
    }

    @Test
    @DisplayName("should delegate pagination to the repository")
    void getFriends_ShouldPassThroughPageable() {
        GetFriendListInput pagedInput = new GetFriendListInput(
                userId,
                0,
                PageRequest.of(0, 20, Sort.by("requestDate")).getPageSize(),
                Sort.by("requestDate")
        );

        when(repository.getFriends(eq(userId), any(FriendsPage.class)))
                .thenReturn(new PageImpl<>(List.of()));

        GetFriendListOutput output = useCase.execute(pagedInput);

        assertNotNull(output);
        verify(repository).getFriends(eq(userId), pageCaptor.capture());
        assertEquals(0, pageCaptor.getValue().page());
        assertEquals(20, pageCaptor.getValue().size());
    }
}
