package com.letraaletra.api.features.levels.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa.SpringDataCosmeticRepository;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper.CosmeticMapper;
import com.letraaletra.api.features.levels.domain.Level;
import com.letraaletra.api.features.levels.domain.LevelReward;
import com.letraaletra.api.features.levels.domain.LevelsPage;
import com.letraaletra.api.features.levels.domain.repository.LevelRepository;
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.entity.LevelJpaEntity;
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.entity.LevelRewardJpaEntity;
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.jpa.SpringDataLevelRepository;
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.jpa.SpringDataLevelRewardRepository;
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.mapper.LevelMapper;
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.mapper.LevelRewardMapper;
import com.letraaletra.api.shared.domain.rewards.CosmeticReward;
import com.letraaletra.api.shared.domain.rewards.HardGemsReward;
import com.letraaletra.api.shared.domain.rewards.Reward;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaLevelRepository implements LevelRepository {
    private final SpringDataLevelRepository repository;
    private final SpringDataLevelRewardRepository levelRewardRepository;
    private final SpringDataCosmeticRepository cosmeticRepository;

    public JpaLevelRepository(
            SpringDataLevelRepository repository,
            SpringDataLevelRewardRepository levelRewardRepository,
            SpringDataCosmeticRepository cosmeticRepository
    ) {
        this.repository = repository;
        this.levelRewardRepository = levelRewardRepository;
        this.cosmeticRepository = cosmeticRepository;
    }

    @Override
    public Page<Level> get(LevelsPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        return repository.findAll(pageable)
                .map(entity -> LevelMapper.toDomain(
                        entity,
                        loadRewards(entity.getId())));
    }

    @Override
    public boolean existsByLevel(int level) {
        return repository.findByLevel(level).isPresent();
    }

    @Override
    public void delete(Level level) {
        LevelJpaEntity entity = LevelMapper.toEntity(level);

        repository.delete(entity);
    }

    @Override
    public Optional<Level> find(UUID id) {
        return repository.findById(id)
                .map(entity -> LevelMapper.toDomain(
                        entity,
                        loadRewards(id)
                ));
    }

    @Override
    public Optional<Level> findByLevel(int level) {
        return repository.findByLevel(level)
                .map(entity -> LevelMapper.toDomain(
                        entity,
                        loadRewards(entity.getId())
                ));
    }

    @Override
    public int findBiggestLevel() {
        return Optional.ofNullable(repository.findBiggestLevel())
                .orElse(0);
    }

    @Override
    @Transactional
    public void save(Level level) {
        repository.save(LevelMapper.toEntity(level));

        levelRewardRepository.deleteByLevelId(level.getLevelId());

        List<LevelRewardJpaEntity> rewards = level.getRewards()
                .stream()
                .map(reward -> LevelRewardMapper.toEntity(level.getLevelId(), reward))
                .toList();

        levelRewardRepository.saveAll(rewards);
    }

    private List<LevelReward> loadRewards(UUID levelId) {
        return levelRewardRepository.findByLevelId(levelId)
                .stream()
                .map(entity -> {

                    Reward reward = switch (entity.getRewardType()) {
                        case COSMETIC -> new CosmeticReward(
                                CosmeticMapper.toDomain(
                                        cosmeticRepository.findById(entity.getRewardReference())
                                                .orElseThrow(CosmeticNotFoundException::new)
                                )
                        );

                        case COIN -> new SoftCoinsReward(entity.getQuantity());

                        case GEMS -> new HardGemsReward(entity.getQuantity());
                    };

                    return LevelRewardMapper.toDomain(entity, reward);
                })
                .toList();
    }
}
