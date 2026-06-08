package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.repository.CosmeticRepository;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa.SpringDataCosmeticRepository;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper.CosmeticMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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
    public Optional<Cosmetic> find(String cosmeticId) {
        return repository.findById(cosmeticId).map(CosmeticMapper::toDomain);
    }
}
