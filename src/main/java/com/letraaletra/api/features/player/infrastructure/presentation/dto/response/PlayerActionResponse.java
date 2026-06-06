package com.letraaletra.api.features.player.infrastructure.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.features.game.domain.event.Event;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.response.game.GameStateDTO;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;

import java.time.Instant;
import java.util.List;

@JsonTypeName("PLAYER_ACTION_RESULT")
public record PlayerActionResponse(
        Instant turnEndsAt,
        List<Event> events,
        GameStateDTO data
) implements WsResponse {
}
