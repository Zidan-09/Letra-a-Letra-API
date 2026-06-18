package com.letraaletra.api.features.friend.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.infrastructure.persistence.postgres.entity.FriendJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataFriendRepository extends JpaRepository<FriendJpaEntity, UUID> {
    @Query("SELECT r FROM FriendJpaEntity r WHERE (r.friendId.userId1 = :userId OR r.friendId.userId2 = :userId) AND r.status = :status")
    List<FriendJpaEntity> getFriendsList(
            @Param("userId") UUID userId,
            @Param("status")FriendStatus status
    );

    @Query("SELECT r FROM FriendJpaEntity r WHERE r.friendId.userId1 = :userId1 AND r.friendId.userId2 = :userId2 OR r.friendId.userId1 = :userId2 AND r.friendId.userId2 = :userId1")
    FriendJpaEntity getFriend(
            @Param("userId1") UUID userId1,
            @Param("userId2") UUID userId2
    );
}
