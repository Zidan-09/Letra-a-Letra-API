package com.letraaletra.api.presentation.dto.response.websocket;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "event"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameUpdatedWsResponse.class, name = "GAME_UPDATED"),
        @JsonSubTypes.Type(value = GameStartedWsResponse.class, name = "GAME_STARTED"),
        @JsonSubTypes.Type(value = GameStateUpdatedWsResponse.class, name = "GAME_STATE_UPDATED"),
        @JsonSubTypes.Type(value = GameOverWsResponse.class, name = "GAME_OVER"),
        @JsonSubTypes.Type(value = RoomClosedResponse.class, name = "GAME_CLOSED"),
        @JsonSubTypes.Type(value = ErrorWsResponse.class, name = "ERROR")
})
public sealed interface WsResponseDTO
    permits
        GameUpdatedWsResponse,
        GameStartedWsResponse,
        GameStateUpdatedWsResponse,
        GameOverWsResponse,
        RoomClosedResponse,
        ErrorWsResponse
{}
