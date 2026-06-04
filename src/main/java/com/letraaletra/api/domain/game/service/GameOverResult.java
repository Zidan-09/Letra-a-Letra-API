package com.letraaletra.api.domain.game.service;

import com.letraaletra.api.features.player.domain.Player;

public record GameOverResult(
        boolean finished,
        Player winner,
        Player loser
) {}