package com.letraaletra.api.infra.repository;

import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.domain.user.exceptions.UserNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    public Map<String, User> findMapByIds(List<String> ids) {
        return ids.stream()
                .map(id -> {
                    User user = userMap.get(id);
                    if (user == null) throw new UserNotFoundException();
                    return user;
                })
                .collect(Collectors.toMap(User::getId, u -> u));
    }

    @Override
    public User findByEmail(String email) {
        return userMap.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
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
