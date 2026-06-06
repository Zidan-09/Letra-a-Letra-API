package com.letraaletra.api.features.game.infrastructure.loader;

import java.util.List;

public record ThemeJson(
        String id,
        String name,
        List<String> words
) {}

