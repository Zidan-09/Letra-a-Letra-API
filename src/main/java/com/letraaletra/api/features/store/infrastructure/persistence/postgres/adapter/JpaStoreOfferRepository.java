package com.letraaletra.api.features.store.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa.SpringDataCosmeticRepository;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper.CosmeticMapper;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import com.letraaletra.api.features.store.infrastructure.persistence.postgres.entity.StoreOfferJpaEntity;
import com.letraaletra.api.features.store.infrastructure.persistence.postgres.jpa.SpringDataStoreRepository;
import com.letraaletra.api.features.store.infrastructure.persistence.postgres.mapper.StoreOfferMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaStoreOfferRepository implements StoreOfferRepository {
    private final SpringDataStoreRepository repository;
    private final SpringDataCosmeticRepository cosmeticRepository;

    public JpaStoreOfferRepository(
            SpringDataStoreRepository repository,
            SpringDataCosmeticRepository cosmeticRepository
    ) {
        this.repository = repository;
        this.cosmeticRepository = cosmeticRepository;
    }

    public Optional<StoreOffer> findById(UUID offerId) {
        return repository.findById(offerId)
                .map(entity -> {
                   Cosmetic cosmetic = CosmeticMapper.toDomain(
                           cosmeticRepository.findById(entity.getCosmeticId()).orElseThrow()
                   );

                   return StoreOfferMapper.toDomain(entity, cosmetic);
                });
    }

    @Override
    public List<StoreOffer> getActiveOffers() {
        List<StoreOfferJpaEntity> entities = repository.findByActive(true);

        return entities.stream()
                .map(entity -> {
                    Cosmetic cosmetic = CosmeticMapper.toDomain(
                            cosmeticRepository.findById(entity.getCosmeticId())
                                    .orElseThrow(CosmeticNotFoundException::new)
                    );

                    return StoreOfferMapper.toDomain(entity, cosmetic);
                })
                .toList();
    }

    @Override
    public void save(StoreOffer storeOffer) {
        repository.save(StoreOfferMapper.toEntity(storeOffer));
    }
}
