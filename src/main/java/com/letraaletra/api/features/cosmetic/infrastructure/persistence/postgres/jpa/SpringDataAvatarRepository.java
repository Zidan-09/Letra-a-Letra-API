package com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.cosmetic.infrastructure.persistence.postgres.entity.AvatarJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataAvatarRepository extends JpaRepository<AvatarJpaEntity, String> {
    <S extends AvatarJpaEntity> S save(S entity);
}