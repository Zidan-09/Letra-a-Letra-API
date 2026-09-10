package com.letraaletra.api.features.friend.infrastructure.presentation.mapper;

import com.letraaletra.api.features.friend.application.input.GetFriendListInput;
import com.letraaletra.api.features.friend.application.output.GetFriendListOutput;
import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.infrastructure.presentation.dto.response.friend.FriendResponse;
import com.letraaletra.api.shared.infrastructure.presentation.Pageables;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;
import java.util.UUID;

public class GetFriendListMapper {
    private static final Set<String> ALLOWED_SORTS = Set.of("requestDate", "status");
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "requestDate");

    public static GetFriendListInput toInput(UUID userId, Pageable pageable) {
        Pageable pages = Pageables.sanitize(pageable, ALLOWED_SORTS, DEFAULT_SORT);

        return new GetFriendListInput(
                userId,
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<FriendResponse> toResponse(GetFriendListOutput output) {
        Page<Friend> page = output.friends();

        return new PageResponse<>(
                page.getContent().stream()
                        .map(FriendResponseMapper::toResponse)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
