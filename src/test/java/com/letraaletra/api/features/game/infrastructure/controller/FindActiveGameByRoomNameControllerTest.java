package com.letraaletra.api.features.game.infrastructure.controller;

import com.letraaletra.api.features.game.application.input.FindActiveGameByRoomNameInput;
import com.letraaletra.api.features.game.application.output.FindActiveGameByRoomNameOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.room.RoomSettings;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameResponse;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.SuccessResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindActiveGameByRoomNameControllerTest {

    @Mock
    private UseCase<FindActiveGameByRoomNameInput, FindActiveGameByRoomNameOutput> useCase;

    private FindActiveGameByRoomNameController controller;
    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        controller = new FindActiveGameByRoomNameController(useCase);
        principal = new AuthenticatedUser(UUID.randomUUID(), "Jogador Teste", false, false);
    }

    @Test
    void shouldReturnPageOfGames() {
        Game game = Game.create("ABC123", "Casa Azul", new RoomSettings(false, false), GameType.CUSTOM);
        Page<Game> page = new PageImpl<>(List.of(game), PageRequest.of(0, 10), 1);

        when(useCase.execute(any(FindActiveGameByRoomNameInput.class)))
                .thenReturn(new FindActiveGameByRoomNameOutput(page));

        ResponseEntity<SuccessResponse<PageResponse<GameResponse>>> response =
                controller.handle(principal, "casa", PageRequest.of(0, 10));

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().data());
        assertEquals(1, response.getBody().data().content().size());
        assertEquals("Casa Azul", response.getBody().data().content().get(0).gameName());
    }
}
