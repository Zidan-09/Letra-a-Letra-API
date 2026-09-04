package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.UsersPage;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserInventoryRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserStatsRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserWalletRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserProcedureMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserInventoryJpaEntity;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserInventoryJpaMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserJpaMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserStatsJpaMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserWalletJpaMapper;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection.InventoryProjection;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection.UserProjection;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaUserRepository implements UserRepository {

    private final SpringDataUserRepository repository;
    private final SpringDataUserInventoryRepository inventoryRepository;
    private final SpringDataUserWalletRepository walletRepository;
    private final SpringDataUserStatsRepository statsRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JpaUserRepository(
            SpringDataUserRepository repository,
            SpringDataUserInventoryRepository inventoryRepository,
            SpringDataUserWalletRepository walletRepository,
            SpringDataUserStatsRepository statsRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.repository = repository;
        this.inventoryRepository = inventoryRepository;
        this.walletRepository = walletRepository;
        this.statsRepository = statsRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Legacy constructor for unit tests without JdbcTemplate
    public JpaUserRepository(
            SpringDataUserRepository repository,
            SpringDataUserInventoryRepository inventoryRepository,
            SpringDataUserWalletRepository walletRepository,
            SpringDataUserStatsRepository statsRepository
    ) {
        this(repository, inventoryRepository, walletRepository, statsRepository, null);
    }

    @Override
    @Transactional
    public void save(User user) {
        if (jdbcTemplate == null) {
            legacySave(user);
            return;
        }
        try {
            String inventoryJson = UserProcedureMapper.inventoryToJson(user);
            jdbcTemplate.update(
                    "CALL sp_user_save(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb)",
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
                    user.getWallet().getBalance().gems(),
                    inventoryJson
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
        List<UserInventoryJpaEntity> inventoryEntities = user.getInventory().getItems().stream()
                .map(item -> UserInventoryJpaMapper.toEntity(user.getUserId(), item))
                .toList();
        inventoryRepository.deleteAllByUserId(user.getUserId());
        inventoryRepository.saveAll(inventoryEntities);
    }

    @Override
    @Transactional
    public void saveAll(List<User> users) {
        users.forEach(this::save);
    }

    @Override
    public Optional<User> find(UUID id) {
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
                .map(projection ->
                        UserJpaMapper.toDomain(
                                projection,
                                inventoryRepository.findInventory(id)
                        )
                );
    }

    @Override
    public List<User> findUsersById(List<UUID> ids) {
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
        List<InventoryProjection> inventories = inventoryRepository.findInventoryByUserIds(ids);
        return users.stream()
                .map(user -> UserJpaMapper.toDomain(
                        user,
                        inventories.stream()
                                .filter(item -> item.getUserId().equals(user.getUserId()))
                                .toList()
                ))
                .toList();
    }

    @Override
    public Optional<User> findByUsername(String username) {
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
                .map(projection ->
                        UserJpaMapper.toDomain(
                                projection,
                                inventoryRepository.findInventory(projection.getUserId())
                        )
                );
    }

    @Override
    public Optional<User> findByEmail(String email) {
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
                .map(projection ->
                        UserJpaMapper.toDomain(
                                projection,
                                inventoryRepository.findInventory(projection.getUserId())
                        )
                );
    }

    @Override
    public Optional<User> findByGoogleId(String googleId) {
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
                .map(projection ->
                        UserJpaMapper.toDomain(
                                projection,
                                inventoryRepository.findInventory(projection.getUserId())
                        )
                );
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
            long total = rows.get(0).total();
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
        List<UUID> ids = users.stream().map(com.letraaletra.api.features.user.infrastructure.persistence.postgres.projection.UserProjection::getUserId).toList();
        Map<UUID, List<InventoryProjection>> inventories = inventoryRepository.findInventoryByUserIds(ids).stream().collect(Collectors.groupingBy(InventoryProjection::getUserId));
        return users.map(user -> UserJpaMapper.toDomain(user, inventories.getOrDefault(user.getUserId(), List.of())));
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return UserProcedureMapper.toDomain(rs);
    }

    private record UserPageRow(User user, long total) {}
}

