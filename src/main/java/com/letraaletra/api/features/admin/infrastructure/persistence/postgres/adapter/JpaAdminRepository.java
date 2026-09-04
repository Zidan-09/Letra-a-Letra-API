package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminsPage;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminPermissionJpaEntity;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa.SpringDataAdminRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa.SpringDataPermissionRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.mapper.AdminJpaMapper;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.mapper.AdminProcedureMapper;
import com.letraaletra.api.infrastructure.persistence.ProcedureExceptionTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaAdminRepository implements AdminRepository {
    private final SpringDataAdminRepository repository;
    private final SpringDataPermissionRepository permissionRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JpaAdminRepository(
            SpringDataAdminRepository repository,
            SpringDataPermissionRepository permissionRepository,
            @Autowired(required = false) JdbcTemplate jdbcTemplate
    ) {
        this.repository = repository;
        this.permissionRepository = permissionRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public JpaAdminRepository(
            SpringDataAdminRepository repository,
            SpringDataPermissionRepository permissionRepository
    ) {
        this(repository, permissionRepository, null);
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
    public Optional<Admin> find(UUID adminId) {
        if (jdbcTemplate == null) {
            return legacyFind(adminId);
        }
        try {
            List<Admin> result = jdbcTemplate.query(
                    "SELECT * FROM sp_admin_find_by_id(?)",
                    (rs, rowNum) -> AdminProcedureMapper.toDomain(rs),
                    adminId
            );
            return result.stream().findFirst();
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyFind(adminId);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private Optional<Admin> legacyFind(UUID adminId) {
        return repository.findById(adminId).map(entity -> {
            List<AdminPermissionJpaEntity> permissions = permissionRepository.findByIdAdminId(adminId);
            return AdminJpaMapper.toDomain(entity, permissions);
        });
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        if (jdbcTemplate == null) {
            return legacyFindByEmail(email);
        }
        try {
            List<Admin> result = jdbcTemplate.query(
                    "SELECT * FROM sp_admin_find_by_email(?)",
                    (rs, rowNum) -> AdminProcedureMapper.toDomain(rs),
                    email
            );
            return result.stream().findFirst();
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyFindByEmail(email);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private Optional<Admin> legacyFindByEmail(String email) {
        return repository.findByEmail(email).map(entity -> {
            List<AdminPermissionJpaEntity> permissions = permissionRepository.findByIdAdminId(entity.getId());
            return AdminJpaMapper.toDomain(entity, permissions);
        });
    }

    @Override
    public Page<Admin> getAdmins(AdminsPage page) {
        if (jdbcTemplate == null) {
            return legacyGetAdmins(page);
        }
        try {
            int limit = page.size();
            int offset = page.page() * page.size();
            List<AdminPageRow> rows = jdbcTemplate.query(
                    "SELECT * FROM sp_admin_find_page(?, ?)",
                    (rs, rowNum) -> {
                        Admin a = AdminProcedureMapper.toDomain(rs);
                        long total = rs.getLong("total_count");
                        return new AdminPageRow(a, total);
                    },
                    limit, offset
            );
            if (rows.isEmpty()) {
                Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());
                return new PageImpl<>(List.of(), pageable, 0);
            }
            long total = rows.get(0).total();
            List<Admin> content = rows.stream().map(AdminPageRow::admin).toList();
            Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());
            return new PageImpl<>(content, pageable, total);
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyGetAdmins(page);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private Page<Admin> legacyGetAdmins(AdminsPage page) {
        Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());
        var admins = repository.findAll(pageable);
        List<UUID> adminIds = admins.stream().map(e -> e.getId()).toList();
        Map<UUID, List<AdminPermissionJpaEntity>> permissionsByAdmin = permissionRepository.findByIdAdminIdIn(adminIds).stream().collect(Collectors.groupingBy(p -> p.getId().getAdminId()));
        return admins.map(entity -> AdminJpaMapper.toDomain(entity, permissionsByAdmin.getOrDefault(entity.getId(), List.of())));
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public void save(Admin admin) {
        if (jdbcTemplate == null) {
            legacySave(admin);
            return;
        }
        try {
            String permsJson = AdminProcedureMapper.permissionsToJson(admin);
            jdbcTemplate.update(
                    "CALL sp_admin_save(?, ?, ?, ?, ?, ?, ?::jsonb)",
                    admin.getId(),
                    admin.getName(),
                    admin.getEmail(),
                    admin.getPasswordHash(),
                    admin.getTokenVersion(),
                    admin.isSuper(),
                    permsJson
            );
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) {
                legacySave(admin);
                return;
            }
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private void legacySave(Admin admin) {
        repository.save(AdminJpaMapper.toEntity(admin));
        permissionRepository.deleteByIdAdminId(admin.getId());
        permissionRepository.saveAll(AdminJpaMapper.toPermissionEntities(admin));
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void delete(Admin admin) {
        repository.delete(AdminJpaMapper.toEntity(admin));
    }

    private record AdminPageRow(Admin admin, long total) {}
}

