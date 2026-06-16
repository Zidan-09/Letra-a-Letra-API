package com.letraaletra.api.features.store.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa.SpringDataCosmeticRepository;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper.CosmeticMapper;
import com.letraaletra.api.features.store.domain.StoreOffer;
import com.letraaletra.api.features.store.domain.exception.OfferNotFoundException;
import com.letraaletra.api.features.store.domain.repository.StoreOfferRepository;
import com.letraaletra.api.features.store.infrastructure.persistence.postgres.entity.StoreOfferJpaEntity;
import com.letraaletra.api.features.store.infrastructure.persistence.postgres.jpa.SpringDataStoreRepository;
import com.letraaletra.api.features.store.infrastructure.persistence.postgres.mapper.StoreOfferMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    public StoreOffer findById(String offerId) {
        StoreOfferJpaEntity entity = repository.findById(UUID.fromString(offerId))
                .orElseThrow(OfferNotFoundException::new);

        Cosmetic cosmetic = CosmeticMapper.toDomain(cosmeticRepository.findById(entity.getCosmeticId().toString())
                .orElseThrow(CosmeticNotFoundException::new));

        return StoreOfferMapper.toDomain(entity, cosmetic);
    }

    @Override
    public List<StoreOffer> getActiveOffers() {
        return List.of();
    }

    @Override
    public void save(StoreOffer storeOffer) {
        repository.save(StoreOfferMapper.toEntity(storeOffer));
    }
}
