package com.letraaletra.api.features.inventory.domain.repository;

import com.letraaletra.api.features.inventory.domain.ItemCategory;

public interface ItemAssetStorage {
    String upload(byte[] content, String fileName, ItemCategory category);
    String copy(String oldPath, String newName, ItemCategory newCategory);
    void delete(String assetPath);
}
