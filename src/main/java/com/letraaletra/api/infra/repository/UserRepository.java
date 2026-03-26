package com.letraaletra.api.infra.repository;

import com.letraaletra.api.domain.user.User;

import java.util.List;

public interface UserRepository {
    void save(User user);
    User find(String id);
    User findByNickname(String nickname);
    User findByEmail(String email);
    List<User> get();
}
