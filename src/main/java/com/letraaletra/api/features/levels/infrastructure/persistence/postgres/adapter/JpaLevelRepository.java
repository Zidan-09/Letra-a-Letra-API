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
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.mapper.LevelProcedureMapper;
import com.letraaletra.api.features.levels.infrastructure.persistence.postgres.mapper.LevelRewardMapper;
import com.letraaletra.api.features.reward.domain.CosmeticReward;
import com.letraaletra.api.features.reward.domain.HardGemsReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;
import com.letraaletra.api.infrastructure.persistence.ProcedureExceptionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JpaLevelRepository(
            SpringDataLevelRepository repository,
            SpringDataLevelRewardRepository levelRewardRepository,
            SpringDataCosmeticRepository cosmeticRepository,
            @Autowired(required = false) JdbcTemplate jdbcTemplate
    ) {
        this.repository = repository;
        this.levelRewardRepository = levelRewardRepository;
        this.cosmeticRepository = cosmeticRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public JpaLevelRepository(
            SpringDataLevelRepository repository,
            SpringDataLevelRewardRepository levelRewardRepository,
            SpringDataCosmeticRepository cosmeticRepository
    ) {
        this(repository, levelRewardRepository, cosmeticRepository, null);
    }

    @Override
    public Page<Level> get(LevelsPage page) {
        if (jdbcTemplate == null) {
            Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort().and(Sort.by("level")));
            return repository.findAll(pageable).map(entity -> LevelMapper.toDomain(entity, loadRewards(entity.getId())));
        }
        try {
            int limit = page.size();
            int offset = page.page() * page.size();
            List<LevelPageRow> rows = jdbcTemplate.query(
                    "SELECT * FROM sp_level_find_page(?, ?)",
                    (rs, rowNum) -> {
                        Level l = LevelProcedureMapper.toDomain(rs);
                        long total = rs.getLong("total_count");
                        return new LevelPageRow(l, total);
                    },
                    limit, offset
            );
            if (rows.isEmpty()) {
                Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort().and(Sort.by("level")));
                return new PageImpl<>(List.of(), pageable, 0);
            }
            long total = rows.get(0).total();
            List<Level> content = applySort(rows.stream().map(LevelPageRow::level).toList(), page.sort());
            Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort().and(Sort.by("level")));
            return new PageImpl<>(content, pageable, total);
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) {
                Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort().and(Sort.by("level")));
                return repository.findAll(pageable).map(entity -> LevelMapper.toDomain(entity, loadRewards(entity.getId())));
            }
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private List<Level> applySort(List<Level> content, Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return content;
        }

        java.util.Comparator<Level> combined = null;
        for (Sort.Order order : sort) {
            java.util.Comparator<Level> next = comparatorFor(order.getProperty());
            if (next == null) {
                continue;
            }
            if (order.isDescending()) {
                next = next.reversed();
            }
            combined = combined == null ? next : combined.thenComparing(next);
        }

        if (combined == null) {
            return content;
        }

        return content.stream().sorted(combined).toList();
    }

    private java.util.Comparator<Level> comparatorFor(String property) {
        if ("level".equalsIgnoreCase(property)) {
            return java.util.Comparator.comparingInt(Level::getLevel);
        }
        return null;
    }

    private boolean isProcedureMissing(DataAccessException ex) {
        if (ex instanceof org.springframework.jdbc.BadSqlGrammarException) return true;
        String msg = ex.getMessage();
        if (msg != null) {
            String lower = msg.toLowerCase();
            if (lower.contains("sp_") || lower.contains("h2") || lower.contains("syntax error") || lower.contains("function") || lower.contains("procedure")) return true;
        }
        Throwable cause = ex.getCause();
        while (cause != null) {
            String cmsg = cause.getMessage();
            if (cmsg != null) {
                String lower = cmsg.toLowerCase();
                if (lower.contains("sp_") || lower.contains("h2") || lower.contains("syntax error")) return true;
            }
            cause = cause.getCause();
        }
        return false;
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
        if (jdbcTemplate == null) {
            return repository.findById(id).map(entity -> LevelMapper.toDomain(entity, loadRewards(id)));
        }
        try {
            List<Level> result = jdbcTemplate.query(
                    "SELECT * FROM sp_level_find_by_id(?)",
                    (rs, rowNum) -> LevelProcedureMapper.toDomain(rs),
                    id
            );
            return result.stream().findFirst();
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return repository.findById(id).map(entity -> LevelMapper.toDomain(entity, loadRewards(id)));
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    @Override
    public Optional<Level> findByLevel(int level) {
        if (jdbcTemplate == null) {
            return repository.findByLevel(level).map(entity -> LevelMapper.toDomain(entity, loadRewards(entity.getId())));
        }
        try {
            List<Level> result = jdbcTemplate.query(
                    "SELECT * FROM sp_level_find_by_value(?)",
                    (rs, rowNum) -> LevelProcedureMapper.toDomain(rs),
                    level
            );
            return result.stream().findFirst();
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return repository.findByLevel(level).map(entity -> LevelMapper.toDomain(entity, loadRewards(entity.getId())));
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    @Override
    public int findBiggestLevel() {
        if (jdbcTemplate == null) {
            return Optional.ofNullable(repository.findBiggestLevel()).orElse(0);
        }
        try {
            Integer v = jdbcTemplate.queryForObject("SELECT sp_level_find_biggest()", Integer.class);
            return v != null ? v : 0;
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return Optional.ofNullable(repository.findBiggestLevel()).orElse(0);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    @Override
    @Transactional
    public void save(Level level) {
        if (jdbcTemplate == null) {
            legacySave(level);
            return;
        }
        try {
            String rewardsJson = LevelProcedureMapper.rewardsToJson(level);
            jdbcTemplate.update(
                    "CALL sp_level_save(?, ?, ?::jsonb)",
                    level.getLevelId(),
                    level.getLevel(),
                    rewardsJson
            );
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) {
                legacySave(level);
                return;
            }
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private void legacySave(Level level) {
        repository.save(LevelMapper.toEntity(level));
        levelRewardRepository.deleteByLevelId(level.getLevelId());
        List<LevelRewardJpaEntity> rewards = level.getRewards().stream().map(reward -> LevelRewardMapper.toEntity(level.getLevelId(), reward)).toList();
        levelRewardRepository.saveAll(rewards);
    }

    private List<LevelReward> loadRewards(UUID levelId) {
        return levelRewardRepository.findByLevelId(levelId).stream().map(entity -> {
            Reward reward = switch (entity.getRewardType()) {
                case COSMETIC -> new CosmeticReward(CosmeticMapper.toDomain(cosmeticRepository.findById(entity.getRewardReference()).orElseThrow(CosmeticNotFoundException::new)));
                case COIN -> new SoftCoinsReward(entity.getQuantity());
                case GEMS -> new HardGemsReward(entity.getQuantity());
            };
            return LevelRewardMapper.toDomain(entity, reward);
        }).toList();
    }

    private record LevelPageRow(Level level, long total) {}
}

