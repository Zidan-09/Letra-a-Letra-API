package com.letraaletra.api.features.friend.infrastructure.persistence.postgres.entity;

import com.letraaletra.api.features.friend.domain.FriendStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "\"friend\"")
public class FriendJpaEntity {
    @EmbeddedId
    private FriendId friendId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FriendStatus status;

    @Column(name = "request_date")
    private LocalDateTime requestDate;
}
