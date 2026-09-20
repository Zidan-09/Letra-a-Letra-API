package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.RefreshSessionInput;
import com.letraaletra.api.features.user.application.input.RevokeSessionInput;
import com.letraaletra.api.features.user.application.output.RefreshSessionOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.RefreshSessionRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.AuthUserResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public class RefreshSessionMapper {
    public static RefreshSessionInput toInput(RefreshSessionRequest dto) {
        return new RefreshSessionInput(
                dto.refreshToken()
        );
    }

    public static RevokeSessionInput toLogoutInput(AuthenticatedUser principal) {
        return new RevokeSessionInput(
                principal
        );
    }

    public static AuthUserResponse toResponse(RefreshSessionOutput output) {
        return new AuthUserResponse(
                output.id().toString(),
                output.token(),
                output.refreshToken()
        );
    }
}
