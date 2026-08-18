package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.domain.ban.BanInfo;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.BanInfoResponse;

public class BanInfoResponseMapper {
    public static BanInfoResponse toResponse(BanInfo banInfo) {
        return new BanInfoResponse(
                banInfo.type() != null,
                banInfo.type(),
                banInfo.reason(),
                banInfo.expiresAt()
        );
    }
}
