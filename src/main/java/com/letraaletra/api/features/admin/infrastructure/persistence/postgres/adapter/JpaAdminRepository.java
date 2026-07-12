package com.letraaletra.api.features.admin.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.admin.domain.Admin;
import com.letraaletra.api.features.admin.domain.repository.AdminRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.jpa.SpringDataAdminRepository;
import com.letraaletra.api.features.admin.infrastructure.persistence.postgres.mapper.AdminMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaAdminRepository implements AdminRepository {
    private final SpringDataAdminRepository repository;

    public JpaAdminRepository(
            SpringDataAdminRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public Optional<Admin> find(UUID adminId) {
        return repository.findById(adminId).map(AdminMapper::toDomain);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return repository.findByEmail(email).map(AdminMapper::toDomain);
    }

    @Override
    public List<Admin> getAdmins() {
        return repository.findAll()
                .stream().map(AdminMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public void save(Admin admin) {
        repository.save(AdminMapper.toEntity(admin));
    }

    @Override
    public long count() {
        return repository.count();
    }
}
