package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.SignInRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.SignInResponse;

public class SignInMapper {
    public static SignInInput toInput(SignInRequest dto) {
        return new SignInInput(
                dto.email(),
                dto.password()
        );
    }

    public static SignInResponse toResponse(SignInOutput output) {
        return new SignInResponse(
                output.id().toString(),
                output.token()
        );
    }
}
