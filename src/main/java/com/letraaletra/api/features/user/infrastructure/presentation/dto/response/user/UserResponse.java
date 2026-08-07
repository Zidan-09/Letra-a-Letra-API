package com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user;

import com.letraaletra.api.features.user.domain.stats.UserStats;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String nickname,
        String email,
        BanInfoResponse banInfo,
        UserStats stats,
        List<InventoryItemResponse> equipped,
        WalletResponse wallet
) {
}
