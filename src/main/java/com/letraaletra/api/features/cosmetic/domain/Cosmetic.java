package com.letraaletra.api.features.cosmetic.domain;

public class Cosmetic {
    private final String id;
    private String name;
    private final CosmeticTypes type;
    private final String assetPath;
    private int version;
    private boolean available;

    public Cosmetic(
            String id,
            String name,
            CosmeticTypes type,
            String assetPath,
            int version,
            boolean available
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.assetPath = assetPath;
        this.version = version;
        this.available = available;
    }

    public static Cosmetic create(
            String id,
            String name,
            CosmeticTypes type,
            String assetPath
    ) {
        return new Cosmetic(
                id,
                name,
                type,
                assetPath,
                1,
                true
        );
    }

    public static Cosmetic restore(
            String id,
            String name,
            CosmeticTypes type,
            String assetPath,
            int version,
            boolean available
    ) {
        return new Cosmetic(
            id,
            name,
            type,
            assetPath,
            version,
            available
        );
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CosmeticTypes getType() {
        return type;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public int getVersion() {
        return version;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void incrementVersion() {
        this.version++;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
