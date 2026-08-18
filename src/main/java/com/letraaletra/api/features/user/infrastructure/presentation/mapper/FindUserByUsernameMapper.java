package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.FindUserByUsernameInput;
import com.letraaletra.api.features.user.application.output.FindUserByUsernameOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.FindUserByUsernameResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;

public class FindUserByUsernameMapper {
    public static FindUserByUsernameInput toInput(AuthenticatedUser principal, String username) {
        return new FindUserByUsernameInput(
                principal,
                username
        );
    }

    public static FindUserByUsernameResponse toResponse(FindUserByUsernameOutput output) {
        return new FindUserByUsernameResponse(
                UserResponseMapper.toResponse(output.user())
        );
    }
}
