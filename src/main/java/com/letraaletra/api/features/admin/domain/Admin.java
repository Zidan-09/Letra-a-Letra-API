package com.letraaletra.api.features.admin.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Admin {
    private final UUID id;
    private final String name;
    private final String email;
    private final String hashPassword;
    private final LocalDateTime createdAt;

    public Admin(
            UUID id,
            String name,
            String email,
            String hashPassword,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hashPassword = hashPassword;
        this.createdAt = createdAt;
    }

    public static Admin create(
            String name,
            String email,
            String hashPassword
    ) {
        return new Admin(
                UUID.randomUUID(),
                name,
                email,
                hashPassword,
                LocalDateTime.now()
        );
    }

    public static Admin restore(
            UUID id,
            String name,
            String email,
            String hashPassword,
            LocalDateTime createdAt
    ) {
        return new Admin(
                id,
                name,
                email,
                hashPassword,
                createdAt
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
