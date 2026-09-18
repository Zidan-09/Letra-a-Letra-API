package com.letraaletra.api.features.items.domain.repository;

import com.letraaletra.api.features.items.domain.Item;

import java.util.UUID;

public interface ItemLookup {

    Item getById(UUID itemId);
}
