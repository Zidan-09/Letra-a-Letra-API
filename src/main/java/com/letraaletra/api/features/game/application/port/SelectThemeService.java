package com.letraaletra.api.features.game.application.port;

import java.util.List;

public interface SelectThemeService {
    List<String> select();
    List<String> select(String themeId);
}
