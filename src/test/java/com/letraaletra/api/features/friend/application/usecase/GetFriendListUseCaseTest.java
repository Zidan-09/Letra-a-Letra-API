package com.letraaletra.api.features.friend.application.usecase;

import com.letraaletra.api.features.friend.application.input.GetFriendListInput;
import com.letraaletra.api.features.friend.application.output.GetFriendListOutput;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.friend.infrastructure.presentation.mapper.GetFriendListMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetFriendListUseCaseTest {
    @Mock
    private FriendRepository repository;

    @InjectMocks
    private GetFriendListUseCase useCase;

    private String userId;
    private GetFriendListInput input;

    @BeforeEach
    void setup() {
        userId = UUID.randomUUID().toString();
        input = GetFriendListMapper.toInput(userId);
    }

    @Test
    @DisplayName("should get friends list correctly")
    void getFriends() {
        when(repository.getFriends(UUID.fromString(userId)))
                .thenReturn(List.of());

        GetFriendListOutput output = useCase.execute(input);

        assertNotNull(output);
        assertInstanceOf(List.class, output.friends());
    }
}