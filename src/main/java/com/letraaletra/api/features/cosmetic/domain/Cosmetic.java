package com.letraaletra.api.features.cosmetic.domain;

public class Cosmetic {
    private final String id;
    private String name;
    private final CosmeticTypes type;
    private final String assetPath;
    private int version;

    public Cosmetic(
            String id,
            String name,
            CosmeticTypes type,
            String assetPath,
            int version
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.assetPath = assetPath;
        this.version = version;
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

    public void setName(String name) {
        this.name = name;
    }

    public void incrementVersion() {
        this.version++;
    }
}
