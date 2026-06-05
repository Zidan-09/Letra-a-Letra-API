package com.letraaletra.api.shared.infrastructure.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.BanParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.KickParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.SwapPositionWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.UnbanParticipantWsRequest;
import com.letraaletra.api.presentation.dto.request.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    JsonMapper jsonMapper() {
        return JsonMapper.builder()
                .registerSubtypes(
                        new NamedType(BanParticipantWsRequest.class, "BAN_PARTICIPANT"),
                        new NamedType(KickParticipantWsRequest.class, "KICK_PARTICIPANT"),
                        new NamedType(SwapPositionWsRequest.class, "SWAP_POSITION"),
                        new NamedType(UnbanParticipantWsRequest.class, "UNBAN_PARTICIPANT"),
                        new NamedType(CreateGameWsRequest.class, "CREATE_GAME"),
                        new NamedType(DiscardPowerWsRequest.class, "DISCARD_POWER"),
                        new NamedType(JoinGameWsRequest.class, "JOIN_GAME"),
                        new NamedType(JoinMatchmakingGameWsRequest.class, "MATCHMAKING_GAME"),
                        new NamedType(LeftGameWsRequest.class, "LEFT_GAME"),
                        new NamedType(PlayerActionWsRequest.class, "PLAYER_ACTION"),
                        new NamedType(StartGameWsRequest.class, "START_GAME")
                )
                .build();
    }
}
