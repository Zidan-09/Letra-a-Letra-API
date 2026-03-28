package com.letraaletra.api.domain.game.service;

import com.letraaletra.api.domain.player.Player;

public record GameOverResult(
        boolean finished,
        Player winner,
        Player loser
) {}