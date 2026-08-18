package com.letraaletra.api.features.game.domain;

import com.letraaletra.api.features.player.domain.Player;

public record GameOver(
        GameOverReasons reason,
        Player winner,
        Player loser
) {}