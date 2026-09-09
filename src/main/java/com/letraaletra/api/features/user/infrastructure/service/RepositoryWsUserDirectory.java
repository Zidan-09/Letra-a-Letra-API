package com.letraaletra.api.features.user.infrastructure.service;

import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.infrastructure.websocket.WsUserDirectory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepositoryWsUserDirectory implements WsUserDirectory {
    private final UserRepository userRepository;

    @Override
    public boolean exists(UUID userId) {
        return userRepository.exists(userId);
    }

    @Override
    public Optional<String> describe(UUID userId) {
        return userRepository.find(userId)
                .map(u -> String.format("%s (%s)", u.getUsername(), u.getUserId()));
    }
}
