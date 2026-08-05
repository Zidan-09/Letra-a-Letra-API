package com.letraaletra.api.features.admin.domain;

import com.letraaletra.api.features.admin.domain.permission.Permissions;

import java.time.LocalDateTime;
import java.util.UUID;

public class Admin {
    private final UUID id;
    private String name;
    private String email;
    private String passwordHash;
    private boolean isSuper;
    private final Permissions permissions;
    private final LocalDateTime createdAt;

    private Admin(
            UUID id,
            String name,
            String email,
            String passwordHash,
            boolean isSuper,
            Permissions permissions,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isSuper = isSuper;
        this.permissions = permissions;
        this.createdAt = createdAt;
    }

    public static Admin create(
            String name,
            String email
    ) {
        return new Admin(
                UUID.randomUUID(),
                name,
                email,
                null,
                false,
                new Permissions(),
                LocalDateTime.now()
        );
    }

    public static Admin restore(
            UUID id,
            String name,
            String email,
            String passwordHash,
            boolean isSuper,
            Permissions permissions,
            LocalDateTime createdAt
    ) {
        return new Admin(
                id,
                name,
                email,
                passwordHash,
                isSuper,
                permissions,
                createdAt
        );
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void changePassword(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void activateAccount(String hashPassword) {
        this.passwordHash = hashPassword;
    }

    public void promoteSuperAdmin() {
        isSuper = true;
    }

    public void revokeSuperAdmin() {
        isSuper = false;
    }

    public boolean isSuper() {
        return isSuper;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
