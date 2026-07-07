package com.letraaletra.api.features.admin.domain.repository;

import com.letraaletra.api.features.admin.domain.Admin;

import java.util.Optional;
import java.util.UUID;

public interface FindAdmin {
    Optional<Admin> find(UUID adminId);
    Optional<Admin> findByEmail(String email);
}
