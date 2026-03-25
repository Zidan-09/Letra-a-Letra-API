package com.letraaletra.api.infra.repository;

import com.letraaletra.api.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {
        userMap.put(user.getId(), user);
    }

    @Override
    public User find(String id) {
        return userMap.get(id);
    }

    @Override
    public List<User> get() {
        return List.copyOf(userMap.values());
    }
}
