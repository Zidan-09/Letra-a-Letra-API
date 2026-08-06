package com.letraaletra.api.features.user.domain.repository.user;

public interface UserRepository extends
        SaveUser,
        FindUser,
        CheckIfExists,
        CountUsers,
        GetUsers
{}
