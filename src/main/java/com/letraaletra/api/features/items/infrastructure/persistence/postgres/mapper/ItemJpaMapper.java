package com.letraaletra.api.features.items.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.items.domain.Item;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.entity.ItemJpaEntity;

public class ItemJpaMapper {
    public static ItemJpaEntity toEntity(Item item) {
        ItemJpaEntity entity = new ItemJpaEntity();

        entity.setId(item.getId());
        entity.setName(item.getName());

        if (item instanceof ConsumableItem consumable) {
            entity.setKind(ItemKind.CONSUMABLE);
            entity.setCategory(null);
            entity.setApplicability(null);
            entity.setStackable(true);
            entity.setMaxStack(1000);
            entity.setConsumable(true);
            entity.setEffect(ItemEffectCodec.encode(consumable.getEffect()));
            entity.setAssetPath(null);
        } else if (item instanceof EquippableItem equippable) {
            entity.setKind(ItemKind.EQUIPPABLE);
            entity.setCategory(equippable.getCategory());
            entity.setApplicability(toApplicabilityValue(equippable.getContext()));
            entity.setStackable(false);
            entity.setMaxStack(null);
            entity.setConsumable(false);
            entity.setEffect(null);
            entity.setAssetPath(equippable.getAssetPath());
        } else {
            throw new InvalidItemException();
        }

        entity.setVersion(item.getVersion());
        entity.setAvailable(item.isAvailable());

        return entity;
    }

    public static Item toDomain(ItemJpaEntity entity) {
        if (entity.getKind() == ItemKind.CONSUMABLE) {
            return ConsumableItem.restore(
                    entity.getId(),
                    entity.getName(),
                    entity.getVersion(),
                    entity.isAvailable(),
                    ItemEffectCodec.decode(entity.getEffect())
            );
        }

        return EquippableItem.restore(
                entity.getId(),
                entity.getName(),
                entity.getVersion(),
                entity.isAvailable(),
                toApplicability(entity.getApplicability()),
                entity.getCategory(),
                entity.getAssetPath()
        );
    }

    private static String toApplicabilityValue(EquippableContext context) {
        if (context == null) {
            throw new InvalidItemException();
        }

        return context.name();
    }

    private static EquippableContext toApplicability(String raw) {
        try {
            return EquippableContext.valueOf(raw);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidItemException();
        }
    }
}
