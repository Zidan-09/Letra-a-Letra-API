package com.letraaletra.api.shared.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.CreateGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.JoinGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.LeftGameWsRequest;
import com.letraaletra.api.features.game.infrastructure.presentation.dto.request.StartGameWsRequest;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.request.ExitMatchmakingGameWsRequest;
import com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.request.JoinMatchmakingGameWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.BanParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.KickParticipantWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.SwapPositionWsRequest;
import com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.UnbanParticipantWsRequest;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.DiscardPowerWsRequest;
import com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionWsRequest;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.request.ExitRankingGameWsRequest;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.request.JoinRankingGameWsRequest;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("Registro dinâmico de subtipos WsRequest - todos os 14 tipos do protocolo")
class WsRequestSubtypeRegistrationTest {

    private JsonMapper mapperFor(Class<? extends WsRequest> type) {
        JsonTypeName typeName = type.getAnnotation(JsonTypeName.class);

        return JsonMapper.builder()
                .registerSubtypes(new NamedType(type, typeName.value()))
                .build();
    }

    @ParameterizedTest(name = "{1} -> {0}")
    @CsvSource({
            "CREATE_GAME, com.letraaletra.api.features.game.infrastructure.presentation.dto.request.CreateGameWsRequest",
            "JOIN_GAME, com.letraaletra.api.features.game.infrastructure.presentation.dto.request.JoinGameWsRequest",
            "LEFT_GAME, com.letraaletra.api.features.game.infrastructure.presentation.dto.request.LeftGameWsRequest",
            "START_GAME, com.letraaletra.api.features.game.infrastructure.presentation.dto.request.StartGameWsRequest",
            "BAN_PARTICIPANT, com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.BanParticipantWsRequest",
            "KICK_PARTICIPANT, com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.KickParticipantWsRequest",
            "SWAP_POSITION, com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.SwapPositionWsRequest",
            "UNBAN_PARTICIPANT, com.letraaletra.api.features.participant.infrastructure.presentation.dto.request.UnbanParticipantWsRequest",
            "PLAYER_ACTION, com.letraaletra.api.features.player.infrastructure.presentation.dto.request.PlayerActionWsRequest",
            "DISCARD_POWER, com.letraaletra.api.features.player.infrastructure.presentation.dto.request.DiscardPowerWsRequest",
            "MATCHMAKING_GAME, com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.request.JoinMatchmakingGameWsRequest",
            "EXIT_MATCHMAKING, com.letraaletra.api.features.matchmaking.infrastructure.presentation.dto.request.ExitMatchmakingGameWsRequest",
            "RANKING_GAME, com.letraaletra.api.features.ranking.infrastructure.presentation.dto.request.JoinRankingGameWsRequest",
            "EXIT_RANKING, com.letraaletra.api.features.ranking.infrastructure.presentation.dto.request.ExitRankingGameWsRequest"
    })
    void shouldDecodeEveryProtocolType(String typeName, String className) throws Exception {
        @SuppressWarnings("unchecked")
        Class<? extends WsRequest> type = (Class<? extends WsRequest>) Class.forName(className);

        Object decoded = mapperFor(type).readValue(
                "{\"type\":\"" + typeName + "\"}",
                WsRequest.class
        );

        assertInstanceOf(type, decoded);
    }
}
