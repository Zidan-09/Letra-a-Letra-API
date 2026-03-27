package com.letraaletra.api.domain.repository;

import com.letraaletra.api.domain.user.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {
    void save(User user);
    Map<String, User> findMapByIds(List<String> ids);
    User find(String id);
    boolean existsByNickname(String nickname);
    boolean existsByEmail(String email);
    User findByEmail(String email);
    List<User> get();
}
