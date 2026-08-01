package com.letraaletra.api.features.game.domain.repository;

import com.letraaletra.api.features.game.domain.board.theme.Theme;

import java.util.List;

public interface GetTheme {
    List<Theme> get();
    List<String> getIds();
}
