package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UsersPage;
import org.springframework.data.domain.Page;

public interface GetUsers {
    Page<User> get(UsersPage page);
}
