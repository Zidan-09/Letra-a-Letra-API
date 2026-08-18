package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;

@JsonTypeName("RANKING_GAME")
public record JoinRankingGameWsRequest() implements WsRequest {
    @Override
    public String getAudit() {
        return "joined in ranking queue";
    }
}
