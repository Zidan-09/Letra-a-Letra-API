package com.letraaletra.api.features.items.domain.repository;

import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;

public interface ItemAssetStorage {
    String upload(byte[] content, String fileName, EquippableCategory category);
    String copy(String oldPath, String newName, EquippableCategory newCategory);
    void delete(String assetPath);
}
