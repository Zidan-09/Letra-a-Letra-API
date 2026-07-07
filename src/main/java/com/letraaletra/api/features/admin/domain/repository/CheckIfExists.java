package com.letraaletra.api.features.admin.domain.repository;

public interface CheckIfExists {
    boolean existsByEmail(String email);
}
