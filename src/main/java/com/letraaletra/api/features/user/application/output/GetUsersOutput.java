package com.letraaletra.api.features.user.application.output;

import com.letraaletra.api.features.user.domain.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record GetUsersOutput(
        Page<User> users,
        Map<UUID, List<EquippedItem>> equippedByUser
) {
}
