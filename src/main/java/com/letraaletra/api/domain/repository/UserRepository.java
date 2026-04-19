package com.letraaletra.api.domain.repository;

import com.letraaletra.api.domain.user.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> find(String id);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    Optional<User> findByGoogleId(String googleId);
}
