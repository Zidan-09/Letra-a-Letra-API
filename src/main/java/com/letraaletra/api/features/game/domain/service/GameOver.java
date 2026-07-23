package com.letraaletra.api.features.game.domain.service;

import com.letraaletra.api.features.game.domain.GameOverReasons;
import com.letraaletra.api.features.player.domain.Player;

public record GameOver(
        GameOverReasons reason,
        Player winner,
        Player loser
) {}