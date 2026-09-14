package com.letraaletra.api.features.inventory.domain;

import java.util.UUID;

public interface ItemDefinitionLookup {

    ItemDefinition findById(UUID definitionId);
}
