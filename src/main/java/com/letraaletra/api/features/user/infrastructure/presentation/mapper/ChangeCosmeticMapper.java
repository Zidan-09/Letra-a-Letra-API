package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.ChangeCosmeticInput;
import com.letraaletra.api.features.user.application.output.ChangeCosmeticOutput;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.request.ChangeCosmeticRequest;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.ChangeCosmeticResponse;
import org.springframework.stereotype.Component;

@Component
public class ChangeCosmeticMapper {
    public static ChangeCosmeticInput toInput(ChangeCosmeticRequest request, String userId) {
        return new ChangeCosmeticInput(
                userId,
                request.cosmeticId()
        );
    }

    public static ChangeCosmeticResponse toResponse(ChangeCosmeticOutput output) {
        return new ChangeCosmeticResponse(
                output.inventory()
        );
    }
}
