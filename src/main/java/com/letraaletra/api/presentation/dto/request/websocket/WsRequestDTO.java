package com.letraaletra.api.presentation.dto.request.websocket;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateGameWsRequest.class, name = "CREATE_GAME"),
        @JsonSubTypes.Type(value = JoinGameWsRequest.class, name = "JOIN_GAME"),
        @JsonSubTypes.Type(value = SwapPlaceWsRequest.class, name = "SWAP_PLACE"),
        @JsonSubTypes.Type(value = StartGameWsRequest.class, name = "START_GAME"),
        @JsonSubTypes.Type(value = LeftGameWsRequest.class, name = "LEFT_GAME"),
        @JsonSubTypes.Type(value = KickParticipantWsRequest.class, name = "KICK_PARTICIPANT"),
        @JsonSubTypes.Type(value = BanParticipantWsRequest.class, name = "BAN_PARTICIPANT"),
        @JsonSubTypes.Type(value = UnbanParticipantWsRequest.class, name = "UNBAN_USER"),
        @JsonSubTypes.Type(value = PlayerActionWsRequest.class, name = "PLAYER_ACTION")
})

public sealed interface WsRequestDTO
    permits
        CreateGameWsRequest,
        JoinGameWsRequest,
        SwapPlaceWsRequest,
        StartGameWsRequest,
        LeftGameWsRequest,
        KickParticipantWsRequest,
        BanParticipantWsRequest,
        UnbanParticipantWsRequest,
        PlayerActionWsRequest
{}
