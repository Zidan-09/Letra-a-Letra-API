package com.letraaletra.api.dto.request.websocket;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateGameWsRequest.class, name = "CREATE_GAME"),
        @JsonSubTypes.Type(value = JoinGameWsRequest.class, name = "JOIN_GAME"),
        @JsonSubTypes.Type(value = StartGameWsRequest.class, name = "START_GAME"),
        @JsonSubTypes.Type(value = PlayerActionWsRequest.class, name = "PLAYER_ACTION")
})

public sealed interface WsRequestDTO
    permits CreateGameWsRequest, JoinGameWsRequest, StartGameWsRequest, PlayerActionWsRequest {
}
