package com.letraaletra.api.features.friend.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.friend.domain.Friend;
import com.letraaletra.api.features.friend.domain.FriendStatus;
import com.letraaletra.api.features.friend.domain.FriendsPage;
import com.letraaletra.api.features.friend.domain.repository.FriendRepository;
import com.letraaletra.api.features.friend.infrastructure.persistence.postgres.entity.FriendJpaEntity;
import com.letraaletra.api.features.friend.infrastructure.persistence.postgres.jpa.SpringDataFriendRepository;
import com.letraaletra.api.features.friend.infrastructure.persistence.postgres.mapper.FriendMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaFriendRepository implements FriendRepository {
    private final Logger logger = LoggerFactory.getLogger(JpaFriendRepository.class);
    private final SpringDataFriendRepository repository;

    public JpaFriendRepository(
            SpringDataFriendRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Page<Friend> getFriends(UUID userId, FriendsPage page) {
        Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());

        return repository.getFriendsList(userId, FriendStatus.ACCEPT, pageable)
                .map(FriendMapper::toDomain);
    }

    @Override
    public List<Friend> getPendingRequests(UUID userId) {
        return repository.getReceivedPendingRequests(userId, FriendStatus.PENDING)
                .stream().map(FriendMapper::toDomain)
                .toList();
    }

    @Override
    public List<Friend> getSentPendingRequests(UUID userId) {
        return repository.getSentPendingRequests(userId, FriendStatus.PENDING)
                .stream().map(FriendMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Friend> find(UUID userId1, UUID userId2) {
        List<FriendJpaEntity> matches = repository.findPair(userId1, userId2, FriendStatus.PENDING);

        if (matches.isEmpty()) {
            return Optional.empty();
        }

        if (matches.size() > 1) {
            logger.warn("Duplicate friendship rows detected for pair ({}, {}): {} rows. Resolving deterministically.",
                    userId1, userId2, matches.size());
        }

        return Optional.of(FriendMapper.toDomain(matches.getFirst()));
    }

    @Override
    public void save(Friend friend) {
        repository.save(FriendMapper.toEntity(friend));
    }
}
