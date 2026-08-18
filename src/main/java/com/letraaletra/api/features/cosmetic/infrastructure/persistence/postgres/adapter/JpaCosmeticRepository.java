package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.cosmetic.domain.CosmeticsPage;
import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.CosmeticJpaEntity;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa.SpringDataCosmeticRepository;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper.CosmeticMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaCosmeticRepository implements CosmeticRepository {
    private final SpringDataCosmeticRepository repository;

    public JpaCosmeticRepository(
        SpringDataCosmeticRepository springDataCosmeticRepository
    ) {
        this.repository = springDataCosmeticRepository;
    }

    @Override
    public void save(Cosmetic cosmetic) {
        repository.save(CosmeticMapper.toEntity(cosmetic));
    }

    @Override
    public Optional<Cosmetic> find(UUID cosmeticId) {
        return repository.findById(cosmeticId).map(CosmeticMapper::toDomain);
    }

    @Override
    public Optional<Cosmetic> findByName(String name) {
        return repository.findByName(name).map(CosmeticMapper::toDomain);
    }

    @Override
    public Page<Cosmetic> search(String search, CosmeticsPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        return repository.search(search, pageable)
                .map(CosmeticMapper::toDomain);
    }

    @Override
    public Page<Cosmetic> get(CosmeticsPage page) {
        Pageable pageable = PageRequest.of(
                page.page(),
                page.size(),
                page.sort()
        );

        return repository.findAll(pageable).map(CosmeticMapper::toDomain);
    }

    @Override
    public void delete(Cosmetic cosmetic) {
        CosmeticJpaEntity entity = CosmeticMapper.toEntity(cosmetic);

        repository.delete(entity);
    }

    @Override
    public boolean checkIfExistsByName(String name) {
        return repository.findByName(name).isPresent();
    }
}
