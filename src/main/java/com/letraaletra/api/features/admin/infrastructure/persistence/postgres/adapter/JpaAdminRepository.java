package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.AdminsPage;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminJpaEntity;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.entity.AdminPermissionJpaEntity;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa.SpringDataAdminRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa.SpringDataPermissionRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.mapper.AdminMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public JpaAdminRepository(
            SpringDataAdminRepository repository,
            SpringDataPermissionRepository permissionRepository
    ) {
        this.repository = repository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Optional<Admin> find(UUID adminId) {
        return repository.findById(adminId)
                .map(entity -> {
                    List<AdminPermissionJpaEntity> permissions =
                            permissionRepository.findByIdAdminId(adminId);

                    return AdminMapper.toDomain(entity, permissions);
                });
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return repository.findByEmail(email)
                .map(entity -> {
                    List<AdminPermissionJpaEntity> permissions =
                            permissionRepository.findByIdAdminId(entity.getId());

                    return AdminMapper.toDomain(entity, permissions);
                });
    }

    @Override
    public Page<Admin> getAdmins(AdminsPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        Page<AdminJpaEntity> admins = repository.findAll(pageable);

        List<UUID> adminIds = admins.stream()
                .map(AdminJpaEntity::getId)
                .toList();

        Map<UUID, List<AdminPermissionJpaEntity>> permissionsByAdmin =
                permissionRepository.findByIdAdminIdIn(adminIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                permission -> permission.getId().getAdminId()
                        ));

        return admins.map(entity ->
                AdminMapper.toDomain(
                        entity,
                        permissionsByAdmin.getOrDefault(
                                entity.getId(),
                                List.of()
                        )
                )
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public void save(Admin admin) {
        repository.save(AdminMapper.toEntity(admin));

        permissionRepository.deleteByIdAdminId(admin.getId());

        permissionRepository.saveAll(
                AdminMapper.toPermissionEntities(admin)
        );
    }

    @Override
    public long count() {
        return repository.count();
    }
}
