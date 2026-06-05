package com.letraaletra.api.presentation.mappers.user;

import com.letraaletra.api.application.command.auth.AuthInput;
import com.letraaletra.api.presentation.dto.request.user.GoogleAuthRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class GoogleAuthMapper {
    public AuthInput toCommand(GoogleAuthRequestDTO request) {
        return new AuthInput(
                request.token()
        );
    }
}
