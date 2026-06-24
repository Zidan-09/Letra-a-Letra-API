package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.GetFriendListInput;
import com.letraaletra.api.features.friend.application.output.GetFriendListOutput;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.GetFriendListResponse;

import java.util.UUID;

public class GetFriendListMapper {
    public static GetFriendListInput toInput(String userId) {
        return new GetFriendListInput(
                UUID.fromString(userId)
        );
    }

    public static GetFriendListResponse toResponse(GetFriendListOutput output) {
        return new GetFriendListResponse(
                output.friends()
        );
    }
}
