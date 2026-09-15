package com.letraaletra.api.features.user.application.output;

import com.letraaletra.api.features.user.domain.User;

import java.util.List;

public record GetMyProfileOutput(
        User user,
        List<EquippedItem> equipped
) {
}
