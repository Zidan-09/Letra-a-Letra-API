package com.letraaletra.api.features.user.domain.repository;

public interface CheckIfExists {
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}
