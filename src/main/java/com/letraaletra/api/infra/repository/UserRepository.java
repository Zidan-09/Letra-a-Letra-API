package com.letraaletra.api.infra.repository;

import com.letraaletra.api.domain.user.User;

import java.util.List;

public interface UserRepository {
    User find(String id);
    List<User> findAll();
}
