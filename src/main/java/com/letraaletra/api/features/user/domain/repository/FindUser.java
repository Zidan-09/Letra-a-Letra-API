package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface FindUser {
    Optional<User> find(UUID id);
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
}
