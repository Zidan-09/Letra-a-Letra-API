package com.letraaletra.api.domain.theme;

import java.util.List;

public class Theme {
    private final String themeId;
    private final String name;
    private final List<String> words;

    public Theme(String themeId, String name, List<String> words) {
        this.themeId = themeId;
        this.name = name;
        this.words = List.copyOf(words);
    }

    public String getThemeId() {
        return themeId;
    }

    public String getName() {
        return name;
    }

    public List<String> getWords() {
        return words;
    }
}
