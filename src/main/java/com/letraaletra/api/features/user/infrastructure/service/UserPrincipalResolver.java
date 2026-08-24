package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.ban.exception.UserBannedFromGameException;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.exception.SessionExpiredException;
import com.letraaletra.api.shared.domain.security.RolePrincipalResolver;
import com.letraaletra.api.shared.domain.security.Roles;
import com.letraaletra.api.shared.domain.security.TokenContent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPrincipalResolver implements RolePrincipalResolver {
    private final UserRepository userRepository;

    @Override
    public Roles role() {
        return Roles.USER;
    }

    @Override
    public AuthenticatedUser resolve(TokenContent content) {
        User user = userRepository.find(content.id())
                .orElseThrow(UserNotFoundException::new);

        if (user.isBanned()) {
            throw new UserBannedFromGameException();
        }

        if (!user.getTokenVersion().equals(content.tokenVersion())) {
            throw new SessionExpiredException();
        }

        return new AuthenticatedUser(user.getUserId(), user.getUsername(), false, false);
    }
}
