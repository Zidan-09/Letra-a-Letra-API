package com.letraaletra.api.features.items.domain;

import com.letraaletra.api.features.items.domain.exception.InvalidItemException;

import java.util.Locale;

public final class ItemSlug {
    private final String value;

    private ItemSlug(String value) {
        this.value = value;
    }

    public static ItemSlug from(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidItemException();
        }

        return new ItemSlug(name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "-"));
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
