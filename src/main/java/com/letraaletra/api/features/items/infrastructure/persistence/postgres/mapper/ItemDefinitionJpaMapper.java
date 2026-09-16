package com.letraaletra.api.features.items.infrastructure.persistence.postgres.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemEffect;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.entity.ItemDefinitionJpaEntity;

public class ItemDefinitionJpaMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ItemDefinitionJpaEntity toEntity(ItemDefinition domain) {
        ItemDefinitionJpaEntity entity = new ItemDefinitionJpaEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setKind(domain.getKind());
        entity.setCategory(domain.getCategory());
        entity.setApplicability(toApplicabilityValue(domain.getContext()));
        entity.setStackable(domain.isStackable());
        entity.setMaxStack(domain.getMaxStack());
        entity.setConsumable(domain.isConsumable());
        entity.setEffect(toEffectJson(domain.getEffect()));
        entity.setAssetPath(domain.getAssetPath());
        entity.setVersion(domain.getVersion());
        entity.setAvailable(domain.isAvailable());

        return entity;
    }

    public static ItemDefinition toDomain(ItemDefinitionJpaEntity entity) {
        return ItemDefinition.restore(
                entity.getId(),
                entity.getName(),
                entity.getKind(),
                entity.getCategory(),
                toApplicability(entity.getApplicability()),
                entity.isStackable(),
                entity.getMaxStack(),
                entity.isConsumable(),
                toEffect(entity.getEffect()),
                entity.getAssetPath(),
                entity.getVersion(),
                entity.isAvailable()
        );
    }

    private static String toApplicabilityValue(ItemContext context) {
        if (context == null) {
            throw new InvalidItemException();
        }

        return context.name();
    }

    private static ItemContext toApplicability(String raw) {
        try {
            return ItemContext.valueOf(raw);
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
