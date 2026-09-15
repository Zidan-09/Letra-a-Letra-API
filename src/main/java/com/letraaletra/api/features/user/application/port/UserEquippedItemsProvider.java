package com.letraaletra.api.features.user.application.port;

import com.letraaletra.api.features.user.application.output.EquippedItem;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserEquippedItemsProvider {
    List<EquippedItem> equipped(UUID ownerId);
    Map<UUID, List<EquippedItem>> equippedFor(Collection<UUID> ownerIds);
}
