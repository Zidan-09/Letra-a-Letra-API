package com.letraaletra.api.features.items.infrastructure.presentation.dto.request;

import com.letraaletra.api.features.items.domain.effect.EffectType;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.item.ItemKind;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateItemRequest(
        @NotBlank String name,
        @NotNull ItemKind kind,
        EquippableCategory category,
        EquippableContext context,
        String effectKind,
        EffectType effectType,
        @Min(1) Integer magnitude,
        @Min(1) Integer durationMinutes,
        MultipartFile asset
) {
}
