package com.letraaletra.api.features.user.infrastructure.presentation.mapper;

import com.letraaletra.api.features.user.application.input.FindUserByUsernameInput;
import com.letraaletra.api.features.user.application.output.FindUserByUsernameOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.InventoryItemResponse;
import com.letraaletra.api.features.user.infrastructure.presentation.dto.response.user.UserResponse;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.infrastructure.presentation.Pageables;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public class FindUserByUsernameMapper {
    private static final Set<String> ALLOWED_SORTS = Set.of("username", "email", "createdAt");

    public static FindUserByUsernameInput toInput(AuthenticatedUser principal, String username, Pageable pageable) {
        Pageable pages = Pageables.sanitize(pageable, ALLOWED_SORTS, Sort.unsorted());

        return new FindUserByUsernameInput(
                principal,
                username,
                pages.getPageNumber(),
                pages.getPageSize(),
                pages.getSort()
        );
    }

    public static PageResponse<UserResponse> toResponse(FindUserByUsernameOutput output, java.util.Map<java.util.UUID, java.util.List<InventoryItemResponse>> equippedByUser) {
        Page<User> page = output.users();

        return new PageResponse<>(
                page.getContent().stream()
                        .map(user -> UserResponseMapper.toResponse(user, equippedByUser == null ? java.util.List.of() : equippedByUser.getOrDefault(user.getUserId(), java.util.List.of())))
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
