package com.letraaletra.api.features.items.domain.repository;

import com.letraaletra.api.features.items.domain.ItemDefinition;

import java.util.UUID;

public interface ItemDefinitionLookup {

    ItemDefinition getById(UUID definitionId);
}
