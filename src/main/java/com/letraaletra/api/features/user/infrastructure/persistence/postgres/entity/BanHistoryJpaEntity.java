package com.letraaletra.api.features.user.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.user.domain.BanType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "ban_history")
public class BanHistoryJpaEntity {
    @Id
    @Column(name = "ban_history_id")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "admin_id")
    private UUID adminId;

    @Column(name = "reason")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private BanType type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "removed_at")
    private LocalDateTime removedAt;

    @Column(name = "removed_by")
    private UUID removedBy;
}
