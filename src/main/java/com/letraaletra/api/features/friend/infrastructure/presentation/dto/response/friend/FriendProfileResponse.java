package com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend;

import com.letraaletra.api.features.user.domain.stats.UserStats;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.BanInfoResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse;

import java.util.List;
import java.util.UUID;

public record FriendProfileResponse(
        UUID userId,
        String nickname,
        boolean inGame,
        UUID currentGameId,
        UserStats stats,
        BanInfoResponse banInfo,
        List<InventoryItemResponse> equipped
) {
}
