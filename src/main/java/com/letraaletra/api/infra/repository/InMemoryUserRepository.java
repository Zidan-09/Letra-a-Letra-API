package com.letraaletra.api.infra.repository;

import com.letraaletra.api.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryUserRepository implements UserRepository {
    @Override
    public User find(String id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }
}
