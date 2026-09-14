package com.letraaletra.api.features.inventory.domain;

import com.letraaletra.api.features.inventory.domain.exception.InvalidItemException;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class ItemDefinition {
    private final UUID id;
    private String name;
    private final ItemKind kind;
    private final ItemCategory category;
    private final Set<ItemContext> applicability;
    private final boolean stackable;
    private final Integer maxStack;
    private final boolean consumable;
    private final ItemEffect effect;
    private String assetPath;
    private int version;
    private boolean available;

    public ItemDefinition(
            UUID id,
            String name,
            ItemKind kind,
            ItemCategory category,
            Set<ItemContext> applicability,
            boolean stackable,
            Integer maxStack,
            boolean consumable,
            ItemEffect effect,
            String assetPath,
            int version,
            boolean available
    ) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.category = category;
        this.applicability = applicability;
        this.stackable = stackable;
        this.maxStack = maxStack;
        this.consumable = consumable;
        this.effect = effect;
        this.assetPath = assetPath;
        this.version = version;
        this.available = available;
    }

    public static ItemDefinition create(
            String name,
            ItemKind kind,
            ItemCategory category,
            Set<ItemContext> applicability,
            boolean stackable,
            Integer maxStack,
            boolean consumable,
            ItemEffect effect,
            String assetPath
    ) {
        validate(name, kind, category, applicability, stackable, maxStack, consumable, effect, assetPath);

        return new ItemDefinition(
                UUID.randomUUID(),
                name,
                kind,
                category,
                copyApplicability(applicability),
                stackable,
                maxStack,
                consumable,
                effect,
                assetPath,
                1,
                true
        );
    }

    public static ItemDefinition restore(
            UUID id,
            String name,
            ItemKind kind,
            ItemCategory category,
            Set<ItemContext> applicability,
            boolean stackable,
            Integer maxStack,
            boolean consumable,
            ItemEffect effect,
            String assetPath,
            int version,
            boolean available
    ) {
        return new ItemDefinition(
                id,
                name,
                kind,
                category,
                copyApplicability(applicability),
                stackable,
                maxStack,
                consumable,
                effect,
                assetPath,
                version,
                available
        );
    }

    private static void validate(
            String name,
            ItemKind kind,
            ItemCategory category,
            Set<ItemContext> applicability,
            boolean stackable,
            Integer maxStack,
            boolean consumable,
            ItemEffect effect,
            String assetPath
    ) {
        if (name == null || name.isBlank()) {
            throw new InvalidItemException();
        }

        if (kind == null || category == null) {
            throw new InvalidItemException();
        }

        if (applicability == null || applicability.isEmpty()) {
            throw new InvalidItemException();
        }

        if (consumable != (kind == ItemKind.CONSUMABLE)) {
            throw new InvalidItemException();
        }

        if (effect != null && !consumable) {
            throw new InvalidItemException();
        }

        if (!stackable && maxStack != null) {
            throw new InvalidItemException();
        }

        if (maxStack != null && maxStack < 1) {
            throw new InvalidItemException();
        }

        if (kind == ItemKind.COSMETIC && (assetPath == null || assetPath.isBlank())) {
            throw new InvalidItemException();
        }
    }

    private static Set<ItemContext> copyApplicability(Set<ItemContext> applicability) {
        if (applicability == null || applicability.isEmpty()) {
            return EnumSet.noneOf(ItemContext.class);
        }

        return EnumSet.copyOf(applicability);
    }

    public boolean canStack() {
        return stackable;
    }

    public boolean isConsumable() {
        return consumable;
    }

    public boolean isApplicableTo(ItemContext context) {
        return context != null && applicability.contains(context);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemKind getKind() {
        return kind;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public Set<ItemContext> getApplicability() {
        return EnumSet.copyOf(applicability);
    }

    public boolean isStackable() {
        return stackable;
    }

    public Integer getMaxStack() {
        return maxStack;
    }

    public boolean isAvailable() {
        return available;
    }

    public ItemEffect getEffect() {
        return effect;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public int getVersion() {
        return version;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }

    public void incrementVersion() {
        this.version++;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
