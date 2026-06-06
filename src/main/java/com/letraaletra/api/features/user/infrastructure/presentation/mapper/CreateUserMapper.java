package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.CreateUserInput;
import com.letraaletra.api.features.user.application.output.CreateUserOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.CreateUserRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.CreateUserResponse;

public class CreateUserMapper {
    public static CreateUserInput toInput(CreateUserRequest dto) {
        return new CreateUserInput(
                dto.email(),
                dto.password()
        );
    }

    public static CreateUserResponse toResponse(CreateUserOutput output) {
        return new CreateUserResponse(
              output.id(),
              output.nickname(),
              output.email()
        );
    }
}
