package com.letraaletra.api.features.friend.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.infrastructure.persistence.postgres.entity.FriendId;
import com.letraaletra.api.features.friend.infrastructure.persistence.postgres.entity.FriendJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataFriendRepository extends JpaRepository<FriendJpaEntity, FriendId> {
    @Query("SELECT r FROM FriendJpaEntity r WHERE (r.friendId.userId1 = :userId OR r.friendId.userId2 = :userId) AND r.status = :status")
    Page<FriendJpaEntity> getFriendsList(
            @Param("userId") UUID userId,
            @Param("status")FriendStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT r FROM FriendJpaEntity r
            WHERE (r.friendId.userId1 = :userId1 AND r.friendId.userId2 = :userId2)
               OR (r.friendId.userId1 = :userId2 AND r.friendId.userId2 = :userId1)
            ORDER BY CASE WHEN r.status = :pending THEN 0 ELSE 1 END, r.requestDate DESC
            """)
    List<FriendJpaEntity> findPair(
            @Param("userId1") UUID userId1,
            @Param("userId2") UUID userId2,
            @Param("pending") FriendStatus pending
    );

    @Query("SELECT r FROM FriendJpaEntity r WHERE r.friendId.userId2 = :receiverId AND r.status = :status")
    List<FriendJpaEntity> getReceivedPendingRequests(
            @Param("receiverId") UUID receiverId,
            @Param("status") FriendStatus status
    );

    @Query("SELECT r FROM FriendJpaEntity r WHERE r.friendId.userId1 = :senderId AND r.status = :status")
    List<FriendJpaEntity> getSentPendingRequests(
            @Param("senderId") UUID senderId,
            @Param("status") FriendStatus status
    );
}
