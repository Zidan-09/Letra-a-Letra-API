package com.letraaletra.api.features.user.domain.repository.user;

public interface CheckIfExists {
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
}
