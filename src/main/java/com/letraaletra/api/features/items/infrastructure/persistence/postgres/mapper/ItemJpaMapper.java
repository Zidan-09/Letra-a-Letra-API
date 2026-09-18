package com.letraaletra.api.features.items.infrastructure.persistence.postgres.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letraaletra.api.features.items.domain.*;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.entity.ItemJpaEntity;

public class ItemJpaMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ItemJpaEntity toEntity(Item item) {
        ItemJpaEntity entity = new ItemJpaEntity();

        entity.setId(item.getId());
        entity.setName(item.getName());

        if (item instanceof ConsumableItem consumable) {
            entity.setKind(ItemKind.CONSUMABLE);
            entity.setCategory(consumable.getCategory());
            entity.setApplicability(EquippableContext.PROFILE.name());
            entity.setStackable(true);
            entity.setMaxStack(1000);
            entity.setConsumable(true);
            entity.setEffect(toEffectJson(consumable.getEffect()));
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
                    entity.getCategory(),
                    toApplicability(entity.getApplicability()),
                    toEffect(entity.getEffect())
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

    private static String toEffectJson(ItemEffect effect) {
        if (effect == null) {
            return null;
        }

        try {
            return MAPPER.writeValueAsString(effect);
        } catch (JsonProcessingException e) {
            throw new InvalidItemException();
        }
    }

    private static ItemEffect toEffect(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return MAPPER.readValue(raw, ItemEffect.class);
        } catch (JsonProcessingException e) {
            throw new InvalidItemException();
        }
    }
}
