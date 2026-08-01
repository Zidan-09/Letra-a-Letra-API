package com.letraaletra.api.features.admin.infrastructure.presentation.mapper;

import com.letraaletra.api.features.admin.application.input.ActivateAccountInput;
import com.letraaletra.api.features.admin.infrastructure.presentation.dto.request.ActivateAccountRequest;

public class ActivateAccountMapper {
    public static ActivateAccountInput toInput(String token, ActivateAccountRequest request) {
        return new ActivateAccountInput(
                token,
                request.password()
        );
    }
}
