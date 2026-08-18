package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.AuthUserRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.AuthUserResponse;

public class AuthUserMapper {
    public static SignInInput toInput(AuthUserRequest dto) {
        return new SignInInput(
                dto.email(),
                dto.password()
        );
    }

    public static AuthUserResponse toResponse(SignInOutput output) {
        return new AuthUserResponse(
                output.id().toString(),
                output.token()
        );
    }
}
