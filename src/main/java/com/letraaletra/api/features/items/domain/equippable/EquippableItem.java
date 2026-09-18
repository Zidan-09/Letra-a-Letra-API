package com.letraaletra.api.features.items.domain.equippable;

import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.domain.Item;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

public class EquippableItem extends Item {
    private final EquippableContext context;
    private final EquippableCategory category;
    private String assetPath;

    private EquippableItem(
            UUID itemId,
            String name,
            int version,
            boolean available,
            EquippableContext context,
            EquippableCategory category,
            String assetPath
    ) {
        super(itemId, name, version, available);

        if (context == null || category == null) {
            throw new InvalidItemException();
        }

        if (!category.allowedContexts().contains(context)) {
            throw new InvalidItemException();
        }

        if (!isRelativeAssetPath(assetPath)) {
            throw new InvalidItemException();
        }

        this.context = context;
        this.category = category;
        this.assetPath = assetPath;
    }

    public static EquippableItem create(
            String name,
            EquippableContext context,
            EquippableCategory category,
            String assetPath
    ) {
        return new EquippableItem(
                UUID.randomUUID(),
                name,
                1,
                true,
                context,
                category,
                assetPath
        );
    }

    public static EquippableItem restore(
            UUID itemId,
            String name,
            int version,
            boolean available,
            EquippableContext context,
            EquippableCategory category,
            String assetPath
    ) {
        return new EquippableItem(
                itemId,
                name,
                version,
                available,
                context,
                category,
                assetPath
        );
    }

    private static boolean isRelativeAssetPath(String assetPath) {
        if (assetPath == null || assetPath.isBlank()) {
            return false;
        }

        try {
            return new URI(assetPath).getScheme() == null;
        } catch (URISyntaxException e) {
            return true;
        }
    }

    public EquippableContext getContext() {
        return context;
    }

    public EquippableCategory getCategory() {
        return category;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public void setAssetPath(String assetPath) {
        if (!isRelativeAssetPath(assetPath)) {
            throw new InvalidItemException();
        }

        this.assetPath = assetPath;
    }
}
