package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.ban.BanHistory;
import com.letraaletra.api.features.user.domain.repository.banhistory.BanHistoryRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataBanHistoryRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.BanHistoryJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaBanHistoryRepository implements BanHistoryRepository {
    private final SpringDataBanHistoryRepository repository;

    @Override
    public Optional<BanHistory> findById(UUID banHistoryId) {
        return repository.findById(banHistoryId)
                .map(BanHistoryJpaMapper::toDomain);
    }

    @Override
    public Optional<BanHistory> findActiveByUserId(UUID userId) {
        return repository.findActiveByUserId(userId)
                .map(BanHistoryJpaMapper::toDomain);
    }

    @Override
    public void save(BanHistory banHistory) {
        repository.save(BanHistoryJpaMapper.toEntity(banHistory));
    }
}
