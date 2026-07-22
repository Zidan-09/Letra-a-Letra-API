package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.UserDTO;

public class UserDTOMapper {
    public static UserDTO toDto(User user) {
        return new UserDTO(
            user.getId(),
            user.getNickname(),
            user.getEmail()
        );
    }
}
