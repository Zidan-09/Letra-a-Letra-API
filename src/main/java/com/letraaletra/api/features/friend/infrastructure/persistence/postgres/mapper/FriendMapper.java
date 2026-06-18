package com.letraaletra.api.features.friend.infrastructure.persistence.postgres.mapper;

import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.infrastructure.persistence.postgres.entity.FriendId;
import com.letraaletra.api.features.friend.infrastructure.persistence.postgres.entity.FriendJpaEntity;

import java.util.UUID;

public class FriendMapper {
    public static Friend toDomain(FriendJpaEntity entity) {
        return new Friend(
                entity.getFriendId().getUserId1().toString(),
                entity.getFriendId().getUserId2().toString(),
                entity.getStatus(),
                entity.getRequestDate()
        );
    }

    public static FriendJpaEntity toEntity(Friend domain) {
        FriendJpaEntity entity = new FriendJpaEntity();

        FriendId id = new FriendId();
        id.setUserId1(UUID.fromString(domain.getUserId1()));
        id.setUserId2(UUID.fromString(domain.getUserId2()));

        entity.setFriendId(id);
        entity.setStatus(domain.getStatus());
        entity.setRequestDate(domain.getRequestDate());

        return entity;
    }
}
