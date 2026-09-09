package com.letraaletra.api.features.offers.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.cosmetic.domain.Cosmetic;
import com.letraaletra.api.features.cosmetic.domain.exceptions.CosmeticNotFoundException;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.CosmeticJpaEntity;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa.SpringDataCosmeticRepository;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.mapper.CosmeticMapper;
import com.letraaletra.api.features.offers.domain.Offer;
import com.letraaletra.api.features.offers.domain.OfferReward;
import com.letraaletra.api.features.offers.domain.OffersPage;
import com.letraaletra.api.features.offers.domain.repository.OfferRepository;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.entity.OfferJpaEntity;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.entity.OfferRewardJpaEntity;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.jpa.SpringDataOfferRepository;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.jpa.SpringDataOfferRewardRepository;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.mapper.OfferMapper;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.mapper.OfferProcedureMapper;
import com.letraaletra.api.features.offers.infrastructure.persistence.postgres.mapper.OfferRewardMapper;
import com.letraaletra.api.features.reward.domain.CosmeticReward;
import com.letraaletra.api.features.reward.domain.HardGemsReward;
import com.letraaletra.api.features.reward.domain.Reward;
import com.letraaletra.api.features.reward.domain.RewardType;
import com.letraaletra.api.features.reward.domain.SoftCoinsReward;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class JpaOfferRepository implements OfferRepository {
    private final SpringDataOfferRepository repository;
    private final SpringDataOfferRewardRepository offerRewardRepository;
    private final SpringDataCosmeticRepository cosmeticRepository;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JpaOfferRepository(
            SpringDataOfferRepository repository,
            SpringDataOfferRewardRepository offerRewardRepository,
            SpringDataCosmeticRepository cosmeticRepository,
            @Autowired(required = false) JdbcTemplate jdbcTemplate
    ) {
        this.repository = repository;
        this.offerRewardRepository = offerRewardRepository;
        this.cosmeticRepository = cosmeticRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Legacy constructor for tests without JdbcTemplate
    public JpaOfferRepository(
            SpringDataOfferRepository repository,
            SpringDataOfferRewardRepository offerRewardRepository,
            SpringDataCosmeticRepository cosmeticRepository
    ) {
        this(repository, offerRewardRepository, cosmeticRepository, null);
    }

    public Optional<Offer> findById(UUID offerId) {
        if (jdbcTemplate == null) {
            return legacyFindById(offerId);
        }
        try {
            List<Offer> result = jdbcTemplate.query(
                    "SELECT * FROM sp_offer_find_by_id(?)",
                    (rs, rowNum) -> OfferProcedureMapper.toDomain(rs),
                    offerId
            );
            return result.stream().findFirst();
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyFindById(offerId);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private Optional<Offer> legacyFindById(UUID offerId) {
        return repository.findById(offerId)
                .map(entity -> OfferMapper.toDomain(entity, loadRewards(entity.getId())));
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
    public Page<Offer> get(OffersPage page) {
        if (jdbcTemplate == null) {
            return legacyGet(page);
        }
        try {
            int limit = page.size();
            int offset = page.page() * page.size();
            List<OfferPageRow> rows = jdbcTemplate.query(
                    "SELECT * FROM sp_offer_find_page(?, ?)",
                    (rs, rowNum) -> {
                        Offer o = OfferProcedureMapper.toDomain(rs);
                        long total = rs.getLong("total_count");
                        return new OfferPageRow(o, total);
                    },
                    limit, offset
            );
            if (rows.isEmpty()) {
                Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());
                return new PageImpl<>(List.of(), pageable, 0);
            }
            long total = rows.get(0).total();
            List<Offer> content = rows.stream().map(OfferPageRow::offer).toList();
            Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());
            return new PageImpl<>(content, pageable, total);
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyGet(page);
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private Page<Offer> legacyGet(OffersPage page) {
        Pageable pageable = PageRequest.of(page.page(), page.size(), page.sort());
        Page<OfferJpaEntity> entities = repository.findAll(pageable);
        Map<UUID, List<OfferReward>> rewardsByOffer = loadRewardsGrouped(
                entities.stream().map(OfferJpaEntity::getId).toList()
        );
        return entities.map(entity -> OfferMapper.toDomain(
                entity,
                rewardsByOffer.getOrDefault(entity.getId(), List.of())
        ));
    }

    @Override
    public List<Offer> getActiveOffers() {
        if (jdbcTemplate == null) {
            return legacyGetActive();
        }
        try {
            return jdbcTemplate.query(
                    "SELECT * FROM sp_offer_find_active()",
                    (rs, rowNum) -> OfferProcedureMapper.toDomain(rs)
            );
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) return legacyGetActive();
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private List<Offer> legacyGetActive() {
        List<OfferJpaEntity> entities = repository.findByActive(true);
        Map<UUID, List<OfferReward>> rewardsByOffer = loadRewardsGrouped(
                entities.stream().map(OfferJpaEntity::getId).toList()
        );
        return entities.stream()
                .map(entity -> OfferMapper.toDomain(
                        entity,
                        rewardsByOffer.getOrDefault(entity.getId(), List.of())
                ))
                .toList();
    }

    @Override
    public void save(Offer offer) {
        if (jdbcTemplate == null) {
            legacySave(offer);
            return;
        }
        try {
            String rewardsJson = OfferProcedureMapper.rewardsToJson(offer);
            jdbcTemplate.update(
                    "CALL sp_offer_save(?, ?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb)",
                    offer.getOfferId(),
                    offer.getTitle(),
                    offer.getCoinType().name(),
                    offer.getPrice(),
                    offer.isActive(),
                    offer.isRepeatable(),
                    offer.isHasExpiration(),
                    offer.getExpiresAt() != null ? Timestamp.valueOf(offer.getExpiresAt()) : null,
                    offer.getCreatedAt() != null ? Timestamp.valueOf(offer.getCreatedAt()) : null,
                    rewardsJson
            );
        } catch (DataAccessException ex) {
            if (isProcedureMissing(ex)) {
                legacySave(offer);
                return;
            }
            throw ProcedureExceptionTranslator.translate(ex);
        }
    }

    private void legacySave(Offer offer) {
        OfferJpaEntity entity = OfferMapper.toEntity(offer);
        repository.save(entity);
        offerRewardRepository.deleteByOfferId(entity.getId());
        offerRewardRepository.saveAll(
                offer.getRewards().stream().map(reward -> OfferRewardMapper.toEntity(entity.getId(), reward)).toList()
        );
    }

    private List<OfferReward> loadRewards(UUID offerId) {
        return loadRewardsGrouped(List.of(offerId)).getOrDefault(offerId, List.of());
    }

    private Map<UUID, List<OfferReward>> loadRewardsGrouped(List<UUID> offerIds) {
        if (offerIds.isEmpty()) {
            return Map.of();
        }

        List<OfferRewardJpaEntity> entities = offerRewardRepository.findByOfferIdIn(offerIds);
        Map<UUID, Cosmetic> cosmeticsById = loadCosmetics(entities);

        return entities.stream().collect(Collectors.groupingBy(
                OfferRewardJpaEntity::getOfferId,
                Collectors.mapping(
                        entity -> toReward(entity, cosmeticsById),
                        Collectors.toList()
                )
        ));
    }

    private Map<UUID, Cosmetic> loadCosmetics(List<OfferRewardJpaEntity> entities) {
        List<UUID> cosmeticIds = entities.stream()
                .filter(entity -> entity.getRewardType() == RewardType.COSMETIC)
                .map(OfferRewardJpaEntity::getRewardReference)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (cosmeticIds.isEmpty()) {
            return Map.of();
        }

        return cosmeticRepository.findAllById(cosmeticIds).stream()
                .collect(Collectors.toMap(
                        CosmeticJpaEntity::getId,
                        CosmeticMapper::toDomain
                ));
    }

    private OfferReward toReward(OfferRewardJpaEntity entity, Map<UUID, Cosmetic> cosmeticsById) {
        Reward reward = switch (entity.getRewardType()) {
            case COSMETIC -> {
                Cosmetic cosmetic = cosmeticsById.get(entity.getRewardReference());
                if (cosmetic == null) {
                    throw new CosmeticNotFoundException();
                }
                yield new CosmeticReward(cosmetic);
            }
            case COIN -> new SoftCoinsReward(entity.getQuantity());
            case GEMS -> new HardGemsReward(entity.getQuantity());
        };
        return OfferRewardMapper.toDomain(entity, reward);
    }

    @Override
    public void delete(Offer offer) {
        OfferJpaEntity entity = OfferMapper.toEntity(offer);
        repository.delete(entity);
    }

    @Override
    @Transactional
    public void expireOffers() {
        LocalDateTime now = LocalDateTime.now();
        repository.expireOffers(now);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> findActiveExpiredIds() {
        return repository.findActiveExpiredIds(LocalDateTime.now());
    }

    private record OfferPageRow(Offer offer, long total) {}
}

