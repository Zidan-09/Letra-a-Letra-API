package com.letraaletra.api.features.ranking.application.input;

import com.letraaletra.api.features.queue.domain.OnlineUser;

public record JoinRankingInput(
        OnlineUser onlineUser
) {
}
