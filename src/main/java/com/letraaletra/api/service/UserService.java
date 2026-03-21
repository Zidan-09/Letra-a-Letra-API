package com.letraaletra.api.service;

import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.infra.repository.InMemoryUserRepository;
import com.letraaletra.api.infra.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository inMemoryUserRepository;

    public UserService(UserRepository inMemoryUserRepository) {
        this.inMemoryUserRepository = inMemoryUserRepository;
    }

    public User create(User user) {
        return new User(user.getId());
    }

    public User get(String id) {
        return inMemoryUserRepository.find(id);
    }

    public List<User> getAll() {
        return inMemoryUserRepository.findAll();
    }
}
