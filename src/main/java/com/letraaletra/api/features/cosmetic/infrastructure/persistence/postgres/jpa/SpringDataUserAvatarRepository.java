package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.AvatarJpaEntity;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.UserAvatarId;
import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.UserAvatarJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataUserAvatarRepository extends JpaRepository<UserAvatarJpaEntity, UserAvatarId> {
    <S extends UserAvatarJpaEntity> S save(S entity);
    List<AvatarJpaEntity> findByUserAvatarId_UserId(UUID userId);
}
