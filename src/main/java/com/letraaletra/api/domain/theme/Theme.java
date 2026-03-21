package com.letraaletra.api.domain.theme;

import java.util.List;

public class Theme {
    private final String name;
    private final List<String> words;

    public Theme(String name, List<String> words) {
        this.name = name;
        this.words = List.copyOf(words);
    }

    public String getName() {
        return name;
    }

    public List<String> getWords() {
        return words;
    }
}
