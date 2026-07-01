package com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa;

import com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity.UserStatsJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataUserStatsRepository extends JpaRepository<UserStatsJpaEntity, UUID> {
}
