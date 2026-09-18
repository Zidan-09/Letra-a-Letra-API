package com.letraaletra.api.features.items.domain;

import java.util.UUID;

public class EquippableItem extends Item {
    private ItemContext context;
    private EquippableCategory category;
    private String assetPath;

    private EquippableItem(
            UUID itemId,
            String name,
            int version,
            boolean available,
            ItemContext context,
            EquippableCategory category,
            String assetPath
    ) {
        super(itemId, name, version, available);
        this.context = context;
        this.category = category;
        this.assetPath = assetPath;
    }

    public static EquippableItem create(
            String name,
            ItemContext context,
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
            ItemContext context,
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

    public ItemContext getContext() {
        return context;
    }

    public EquippableCategory getCategory() {
        return category;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public void setContext(ItemContext context) {
        this.context = context;
    }

    public void setCategory(EquippableCategory category) {
        this.category = category;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }
}
