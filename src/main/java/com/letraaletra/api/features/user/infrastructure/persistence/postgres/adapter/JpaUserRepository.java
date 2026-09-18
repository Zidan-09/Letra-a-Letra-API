package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.UsersPage;
import com.letraaletra.api.features.user.domain.effect.UserEffect;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserActiveEffectJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserActiveEffectRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserStatsRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserWalletRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserActiveEffectJpaMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserJpaMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserProcedureMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserStatsJpaMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserWalletJpaMapper;
import com.letraaletra.api.infrastructure.persistence.ProcedureExceptionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository repository;
    private final SpringDataUserWalletRepository walletRepository;
    private final SpringDataUserStatsRepository statsRepository;
    private final SpringDataUserActiveEffectRepository effectsRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JpaUserRepository(
            SpringDataUserRepository repository,
            SpringDataUserWalletRepository walletRepository,
            SpringDataUserStatsRepository statsRepository,
            SpringDataUserActiveEffectRepository effectsRepository,
            @Autowired(required = false) JdbcTemplate jdbcTemplate
    ) {
        this.repository = repository;
        this.walletRepository = walletRepository;
        this.statsRepository = statsRepository;
        this.effectsRepository = effectsRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Legacy constructor for unit tests without JdbcTemplate
    public JpaUserRepository(
            SpringDataUserRepository repository,
            SpringDataUserWalletRepository walletRepository,
            SpringDataUserStatsRepository statsRepository
    ) {
        this(repository, walletRepository, statsRepository, null, null);
    }

    @Override
    @Transactional
    public void save(User user) {
        saveUser(user);
        replaceEffects(user);
    }

    private void replaceEffects(User user) {
        if (effectsRepository == null) {
            return;
        }

        effectsRepository.deleteByUserId(user.getUserId());

        List<UserActiveEffectJpaEntity> entities = user.getActiveEffects().getEffects().stream()
                .map(effect -> UserActiveEffectJpaMapper.toEntity(user.getUserId(), effect))
                .toList();

        effectsRepository.saveAll(entities);
    }

    private User attachEffects(User user) {
        if (effectsRepository == null) {
            return user;
        }

        effectsRepository.findByUserId(user.getUserId()).stream()
                .map(UserActiveEffectJpaMapper::toDomain)
                .forEach(user.getActiveEffects()::add);

        return user;
    }

    private List<User> attachEffects(List<User> users) {
        if (effectsRepository == null || users.isEmpty()) {
            return users;
        }

        Map<UUID, User> byId = new HashMap<>();
        for (User user : users) {
            byId.putIfAbsent(user.getUserId(), user);
        }

        for (UserActiveEffectJpaEntity entity : effectsRepository.findByUserIdIn(byId.keySet())) {
            UserEffect effect = UserActiveEffectJpaMapper.toDomain(entity);
            byId.get(entity.getUserId()).getActiveEffects().add(effect);
        }

        return users;
    }

    private void saveUser(User user) {
        if (jdbcTemplate == null) {
            legacySave(user);
            return;
        }
        try {
            jdbcTemplate.update(
                    "CALL sp_user_save(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getTokenVersion(),
                    user.getGoogleId(),
                    user.canChangeNickname(),
                    user.getCurrentGameId(),
                    user.getCreatedAt() != null ? java.sql.Timestamp.valueOf(user.getCreatedAt()) : null,
                    user.getStats().getTotalMatches(),
                    user.getStats().getTotalWins(),
                    user.getStats().getWinStreak(),
                    user.getStats().getLevel(),
                    user.getStats().getExperience(),
                    user.getStats().getRankingPoints(),
                    user.getWallet().getBalance().coins(),
                    user.getWallet().getBalance().gems()
            );
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) {
                legacySave(user);
                return;
            }
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private boolean isProcedureMissing(DataAccessException ex) {
        if (ex instanceof org.springframework.jdbc.BadSqlGrammarException) return true;
        String msg = ex.getMessage();
        if (msg != null) {
            String lower = msg.toLowerCase();
            if (lower.contains("sp_user") || lower.contains("sp_") || lower.contains("h2") || lower.contains("syntax error") || lower.contains("function") || lower.contains("procedure")) {
                return true;
            }
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

    private void legacySave(User user) {
        repository.save(UserJpaMapper.toEntity(user));
        statsRepository.save(UserStatsJpaMapper.toEntity(user));
        walletRepository.save(UserWalletJpaMapper.toEntity(user));
    }

    @Override
    @Transactional
    public void saveAll(List<User> users) {
        users.forEach(this::save);
    }

    @Override
    public boolean exists(UUID id) {
        return repository.existsById(id);
    }

    @Override
    public Optional<User> find(UUID id) {
        Optional<User> result = findInternal(id);
        result.ifPresent(this::attachEffects);
        return result;
    }

    private Optional<User> findInternal(UUID id) {
        if (jdbcTemplate == null) {
            return legacyFind(id);
        }
        try {
            List<User> result = jdbcTemplate.query(
                    "SELECT * FROM sp_user_find_by_id(?)",
                    (rs, rowNum) -> mapUser(rs),
                    id
            );
            return result.stream().findFirst();
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyFind(id);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private Optional<User> legacyFind(UUID id) {
        return repository.findDetailsById(id)
                .map(UserJpaMapper::toDomain);
    }

    @Override
    public List<User> findUsersById(List<UUID> ids) {
        return attachEffects(findUsersByIdInternal(ids));
    }

    private List<User> findUsersByIdInternal(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        if (jdbcTemplate == null) {
            return legacyFindUsersById(ids);
        }
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM sp_user_find_by_ids(?::uuid[])",
                    ps -> {
                        java.sql.Array array = ps.getConnection().createArrayOf("uuid", ids.toArray());
                        ps.setArray(1, array);
                    },
                    (rs, rowNum) -> mapUser(rs)
            );
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyFindUsersById(ids);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private List<User> legacyFindUsersById(List<UUID> ids) {
        List<com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection.UserProjection> users = repository.findDetailsByIds(ids);
        return users.stream()
                .map(UserJpaMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        Optional<User> result = findByUsernameInternal(username);
        result.ifPresent(this::attachEffects);
        return result;
    }

    private Optional<User> findByUsernameInternal(String username) {
        if (jdbcTemplate == null) {
            return legacyFindByUsername(username);
        }
        try {
            List<User> result = jdbcTemplate.query(
                    "SELECT * FROM sp_user_find_by_username(?)",
                    (rs, rowNum) -> mapUser(rs),
                    username
            );
            return result.stream().findFirst();
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyFindByUsername(username);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private Optional<User> legacyFindByUsername(String username) {
        return repository.findDetailsByUsername(username)
                .map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> result = findByEmailInternal(email);
        result.ifPresent(this::attachEffects);
        return result;
    }

    private Optional<User> findByEmailInternal(String email) {
        if (jdbcTemplate == null) {
            return legacyFindByEmail(email);
        }
        try {
            List<User> result = jdbcTemplate.query(
                    "SELECT * FROM sp_user_find_by_email(?)",
                    (rs, rowNum) -> mapUser(rs),
                    email
            );
            return result.stream().findFirst();
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyFindByEmail(email);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private Optional<User> legacyFindByEmail(String email) {
        return repository.findDetailsByEmail(email)
                .map(UserJpaMapper::toDomain);
    }

    @Override
    public Optional<User> findByGoogleId(String googleId) {
        Optional<User> result = findByGoogleIdInternal(googleId);
        result.ifPresent(this::attachEffects);
        return result;
    }

    private Optional<User> findByGoogleIdInternal(String googleId) {
        if (jdbcTemplate == null) {
            return legacyFindByGoogleId(googleId);
        }
        try {
            List<User> result = jdbcTemplate.query(
                    "SELECT * FROM sp_user_find_by_google_id(?)",
                    (rs, rowNum) -> mapUser(rs),
                    googleId
            );
            return result.stream().findFirst();
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyFindByGoogleId(googleId);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private Optional<User> legacyFindByGoogleId(String googleId) {
        return repository.findDetailsByGoogleId(googleId)
                .map(UserJpaMapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return repository.existsByUsername(nickname);
    }

    @Override
    public long countUsers() {
        return repository.count();
    }

    @Override
    public Page<User> get(UsersPage page) {
        Page<User> result = getInternal(page);
        attachEffects(result.getContent());
        return result;
    }

    private Page<User> getInternal(UsersPage page) {
        if (jdbcTemplate == null) {
            return legacyGet(page);
        }
        try {
            int limit = page.size();
            int offset = page.page() * page.size();
            List<UserPageRow> rows = jdbcTemplate.query(
                    "SELECT * FROM sp_user_find_page(?, ?)",
                    (rs, rowNum) -> {
                        User u = mapUser(rs);
                        long total = rs.getLong("total_count");
                        return new UserPageRow(u, total);
                    },
                    limit, offset
            );
            if (rows.isEmpty()) {
                Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());
                return new PageImpl<>(List.of(), pageable, 0);
            }
            long total = rows.getFirst().total();
            List<User> content = rows.stream().map(UserPageRow::user).toList();
            Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());
            return new PageImpl<>(content, pageable, total);
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyGet(page);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private Page<User> legacyGet(UsersPage page) {
        Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());
        var users = repository.findDetails(pageable);
        return users.map(UserJpaMapper::toDomain);
    }

    @Override
    public Page<User> search(String search, UsersPage page) {
        Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());
        var users = repository.search(search, pageable);
        Page<User> result = users.map(UserJpaMapper::toDomain);
        attachEffects(result.getContent());
        return result;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return UserProcedureMapper.toDomain(rs);
    }

    private record UserPageRow(User user, long total) {}
}

