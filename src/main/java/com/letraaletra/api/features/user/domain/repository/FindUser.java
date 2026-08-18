package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindUser {
    Optional<User> find(UUID id);
    List<User> findUsersById(List<UUID> ids);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
}
