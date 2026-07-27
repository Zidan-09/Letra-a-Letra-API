package com.letraaletra.api.shared.infrastructure.presentation.dto.response.reward;

import com.letraaletra.api.features.cosmetic.infrastructure.presentation.dto.response.cosmetic.CosmeticDTO;
import com.letraaletra.api.features.offers.domain.RewardType;

public record RewardResponse(
        RewardType type,
        Integer amount,
        CosmeticDTO cosmetic
) {
}
