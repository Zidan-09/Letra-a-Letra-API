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
    public User findByEmail(String email) {
        return userMap.values().stream()
                .filter(u -> u.getNickname().equals(email))
                .findFirst() .orElse(null);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return userMap.values().stream()
                .anyMatch(u -> u.getNickname().equals(nickname));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMap.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public List<User> get() {
        return List.copyOf(userMap.values());
    }
}
