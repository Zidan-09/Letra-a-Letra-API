package com.letraaletra.api.features.game.domain.service;

import com.letraaletra.api.features.player.domain.Player;

public record GameOverResult(
        boolean finished,
        Player winner,
        Player loser
) {}