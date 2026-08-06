package com.letraaletra.api.features.user.domain.repository.user;

import com.letraaletra.api.features.user.domain.User;

import java.util.List;

public interface SaveUser {
    void save(User user);
    void saveAll(List<User> users);
}
