package com.letraaletra.api.features.ranking.application.input;

import com.letraaletra.api.shared.domain.OnlineUser;

public record JoinRankingInput(
        OnlineUser onlineUser
) {
}
