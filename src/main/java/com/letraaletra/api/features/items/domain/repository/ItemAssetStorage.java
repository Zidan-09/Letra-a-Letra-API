package com.letraaletra.api.features.items.domain.repository;

import com.letraaletra.api.features.items.domain.ItemCategory;

public interface ItemAssetStorage {
    String upload(byte[] content, String fileName, ItemCategory category);
    String copy(String oldPath, String newName, ItemCategory newCategory);
    void delete(String assetPath);
}
