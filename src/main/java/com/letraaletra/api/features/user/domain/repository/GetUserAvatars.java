package com.letraaletra.api.features.user.domain.repository;

import com.letraaletra.api.features.cosmetic.domain.Avatar;

import java.util.List;

public interface GetUserAvatars {
    List<Avatar> getAvatars(String userId);
}
