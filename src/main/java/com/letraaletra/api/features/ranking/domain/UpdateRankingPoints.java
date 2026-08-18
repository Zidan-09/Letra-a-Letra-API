package com.letraaletra.api.features.ranking.domain;

public record UpdateRankingPoints(
        int before,
        int changed,
        int after
) {
}
