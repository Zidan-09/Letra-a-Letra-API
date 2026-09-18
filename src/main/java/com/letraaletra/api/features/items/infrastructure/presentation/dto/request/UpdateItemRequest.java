package com.letraaletra.api.features.items.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.items.domain.effect.EffectType;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import org.springframework.web.multipart.MultipartFile;

public record UpdateItemRequest(
        String name,
        Boolean available,
        EquippableCategory category,
        EquippableContext context,
        String effectKind,
        EffectType effectType,
        Integer magnitude,
        Integer durationMinutes,
        MultipartFile asset,
        boolean isNewAsset
) {
}
