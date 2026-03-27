package com.letraaletra.api.presentation.dto.mappers;

import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.presentation.dto.response.user.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserDTOMapper {
    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getNickname(),
                user.getAvatar()
        );
    }
}
