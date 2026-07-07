package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa.SpringDataCosmeticRepository;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper.CosmeticMapper;
import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.entity.OfferJpaEntity;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.jpa.SpringDataOfferRepository;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.mapper.OfferMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaOfferRepository implements OfferRepository {
    private final SpringDataOfferRepository repository;
    private final SpringDataCosmeticRepository cosmeticRepository;

    public JpaOfferRepository(
            SpringDataOfferRepository repository,
            SpringDataCosmeticRepository cosmeticRepository
    ) {
        this.repository = repository;
        this.cosmeticRepository = cosmeticRepository;
    }

    public Optional<Offer> findById(UUID offerId) {
        return repository.findById(offerId)
                .map(entity -> {
                   Cosmetic cosmetic = CosmeticMapper.toDomain(
                           cosmeticRepository.findById(entity.getCosmeticId()).orElseThrow()
                   );

                   return OfferMapper.toDomain(entity, cosmetic);
                });
    }

    @Override
    public List<Offer> get(GetOffersInput input) {
        Pageable pageable = PageRequest.of(
                input.page(),
                input.size(),
                input.sort()
        );

        return repository.findAll(pageable).stream().map(entity -> {
                Cosmetic cosmetic = CosmeticMapper.toDomain(
                        cosmeticRepository.findById(entity.getCosmeticId()).orElseThrow()
                );

                return OfferMapper.toDomain(entity, cosmetic);
        })
        .toList();
    }

    @Override
    public List<Offer> getActiveOffers() {
        List<OfferJpaEntity> entities = repository.findByActive(true);

        return entities.stream()
                .map(entity -> {
                    Cosmetic cosmetic = CosmeticMapper.toDomain(
                            cosmeticRepository.findById(entity.getCosmeticId())
                                    .orElseThrow(CosmeticNotFoundException::new)
                    );

                    return OfferMapper.toDomain(entity, cosmetic);
                })
                .toList();
    }

    @Override
    public void save(Offer offer) {
        repository.save(OfferMapper.toEntity(offer));
    }

    @Override
    public void delete(Offer offer) {
        OfferJpaEntity entity = OfferMapper.toEntity(offer);

        repository.delete(entity);
    }
}
