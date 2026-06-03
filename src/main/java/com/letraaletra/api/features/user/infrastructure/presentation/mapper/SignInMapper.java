package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.SignInInput;
import com.letraaletra.api.features.user.application.output.SignInOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.SignInRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.SignInResponse;
import org.springframework.stereotype.Component;

@Component
public class SignInMapper {
    public SignInInput toInput(SignInRequest dto) {
        return new SignInInput(
                dto.email(),
                dto.password()
        );
    }

    public SignInResponse toResponse(SignInOutput output) {
        return new SignInResponse(
                output.id(),
                output.token()
        );
    }
}
