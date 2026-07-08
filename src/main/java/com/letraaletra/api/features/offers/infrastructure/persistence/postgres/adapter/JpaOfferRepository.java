package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa.SpringDataCosmeticRepository;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper.CosmeticMapper;
import com.letraaletra.api.features.offers.application.input.GetOffersInput;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.shared.domain.rewards.CosmeticReward;
import com.letraaletra.api.shared.domain.rewards.HardGemsReward;
import com.letraaletra.api.shared.domain.rewards.Reward;
import com.letraaletra.api.shared.domain.rewards.SoftCoinsReward;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.entity.OfferJpaEntity;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.jpa.SpringDataOfferRepository;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.jpa.SpringDataOfferRewardRepository;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.mapper.OfferMapper;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.mapper.OfferRewardMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaOfferRepository implements OfferRepository {
    private final SpringDataOfferRepository repository;
    private final SpringDataOfferRewardRepository offerRewardRepository;
    private final SpringDataCosmeticRepository cosmeticRepository;

    public JpaOfferRepository(
            SpringDataOfferRepository repository,
            SpringDataOfferRewardRepository offerRewardRepository,
            SpringDataCosmeticRepository cosmeticRepository
    ) {
        this.repository = repository;
        this.offerRewardRepository = offerRewardRepository;
        this.cosmeticRepository = cosmeticRepository;
    }

    public Optional<Offer> findById(UUID offerId) {
        return repository.findById(offerId)
                .map(entity -> OfferMapper.toDomain(
                        entity,
                        loadRewards(entity.getId())
                ));
    }

    @Override
    public List<Offer> get(GetOffersInput input) {
        Pageable pageable = PageRequest.of(
                input.page(),
                input.size(),
                input.sort()
        );

        return repository.findAll(pageable)
                .stream()
                .map(entity -> OfferMapper.toDomain(
                        entity,
                        loadRewards(entity.getId())
                ))
                .toList();
    }

    @Override
    public List<Offer> getActiveOffers() {
        return repository.findByActive(true)
                .stream()
                .map(entity -> OfferMapper.toDomain(
                        entity,
                        loadRewards(entity.getId())
                ))
                .toList();
    }

    @Override
    public void save(Offer offer) {
        OfferJpaEntity entity = OfferMapper.toEntity(offer);

        repository.save(entity);

        offerRewardRepository.deleteByOfferId(entity.getId());

        offerRewardRepository.saveAll(
                offer.getRewards()
                        .stream()
                        .map(reward ->
                                OfferRewardMapper.toEntity(entity.getId(), reward))
                        .toList()
        );
    }

    @Override
    public void delete(Offer offer) {
        OfferJpaEntity entity = OfferMapper.toEntity(offer);

        repository.delete(entity);
    }

    private List<OfferReward> loadRewards(UUID offerId) {
        return offerRewardRepository.findByOfferId(offerId)
                .stream()
                .map(entity -> {

                    Reward reward = switch (entity.getRewardType()) {

                        case COSMETIC -> new CosmeticReward(
                                CosmeticMapper.toDomain(
                                        cosmeticRepository.findById(entity.getRewardReference())
                                                .orElseThrow(CosmeticNotFoundException::new)
                                )
                        );

                        case COIN -> new SoftCoinsReward(entity.getQuantity());

                        case GEMS -> new HardGemsReward(entity.getQuantity());
                    };

                    return OfferRewardMapper.toDomain(entity, reward);

                })
                .toList();
    }
}
