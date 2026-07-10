package com.letraaletra.api.features.ranking.infrastructure.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.letraaletra.api.shared.infrastructure.presentation.dto.request.WsRequest;

@JsonTypeName("EXIT_RANKING")
public record ExitRankingGameWsRequest()
        implements WsRequest {
}
