package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.ItemEffect;
import com.letraaletra.api.features.inventory.domain.exception.InvalidItemException;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity.ItemDefinitionJpaEntity;

import java.util.Set;

public class ItemDefinitionJpaMapper {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static ItemDefinitionJpaEntity toEntity(ItemDefinition domain) {
        ItemDefinitionJpaEntity entity = new ItemDefinitionJpaEntity();

        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setKind(domain.getKind());
        entity.setCategory(domain.getCategory());
        entity.setApplicability(toApplicabilityValue(domain.getApplicability()));
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

    private static String toApplicabilityValue(Set<ItemContext> applicability) {
        if (applicability == null || applicability.size() != 1) {
            throw new InvalidItemException();
        }

        return applicability.iterator().next().name();
    }

    private static Set<ItemContext> toApplicability(String raw) {
        try {
            return Set.of(ItemContext.valueOf(raw));
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
