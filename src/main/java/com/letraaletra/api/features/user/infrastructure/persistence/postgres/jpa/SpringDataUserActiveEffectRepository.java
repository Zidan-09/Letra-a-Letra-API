package com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserActiveEffectJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface SpringDataUserActiveEffectRepository extends JpaRepository<UserActiveEffectJpaEntity, UUID> {
    List<UserActiveEffectJpaEntity> findByUserId(UUID userId);

    List<UserActiveEffectJpaEntity> findByUserIdIn(Collection<UUID> userIds);

    void deleteByUserId(UUID userId);
}
