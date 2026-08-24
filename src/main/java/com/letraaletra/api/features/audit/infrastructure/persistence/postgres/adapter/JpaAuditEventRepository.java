package com.letraaletra.api.features.audit.infrastructure.persistence.postgres.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventFilter;
import com.letraaletra.api.features.audit.domain.repository.FindAuditEvents;
import com.letraaletra.api.features.audit.domain.repository.SaveAuditEvent;
import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.entity.AuditEventJpaEntity;
import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.jpa.SpringDataAuditEventRepository;
import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.mapper.AuditEventJpaMapper;

@Repository
public class JpaAuditEventRepository implements SaveAuditEvent, FindAuditEvents {

    private final SpringDataAuditEventRepository repository;

    public JpaAuditEventRepository(SpringDataAuditEventRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(AuditEvent event) {
        repository.save(AuditEventJpaMapper.toEntity(event));
    }

    @Override
    public Page<AuditEvent> find(AuditEventFilter filter, int page, int size, boolean ascending) {
        Sort sort = Sort.by(
                ascending ? Sort.Direction.ASC : Sort.Direction.DESC,
                "occurredAt",
                "eventId"
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.findAll(toSpecification(filter), pageable)
                .map(AuditEventJpaMapper::toDomain);
    }

    @Override
    public boolean existsByTransactionId(UUID transactionId) {
        return repository.existsByTransactionId(transactionId);
    }

    static Specification<AuditEventJpaEntity> toSpecification(AuditEventFilter filter) {
        List<Specification<AuditEventJpaEntity>> specs = new ArrayList<>();

        if (filter.targetUserId() != null) {
            specs.add(equal("targetUserId", filter.targetUserId()));
        }
        if (filter.actorId() != null) {
            specs.add((root, query, cb) -> cb.equal(root.get("actorId"), filter.actorId()));
        }
        if (filter.eventType() != null) {
            specs.add(equal("eventType", filter.eventType()));
        }
        if (filter.category() != null) {
            specs.add(equal("category", filter.category()));
        }
        if (filter.outcome() != null) {
            specs.add(equal("outcome", filter.outcome()));
        }
        if (filter.resourceType() != null) {
            specs.add(equal("resourceType", filter.resourceType()));
        }
        if (filter.resourceId() != null && !filter.resourceId().isBlank()) {
            specs.add(equal("resourceId", filter.resourceId()));
        }
        if (filter.from() != null) {
            specs.add((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("occurredAt"), filter.from()));
        }
        if (filter.to() != null) {
            specs.add((root, query, cb) -> cb.lessThanOrEqualTo(root.get("occurredAt"), filter.to()));
        }
        if (filter.requestId() != null && !filter.requestId().isBlank()) {
            specs.add(equal("requestId", filter.requestId()));
        }
        if (filter.operationId() != null) {
            specs.add(equal("operationId", filter.operationId()));
        }
        if (filter.correlationId() != null && !filter.correlationId().isBlank()) {
            specs.add(equal("correlationId", filter.correlationId()));
        }
        if (filter.transactionId() != null) {
            specs.add(equal("transactionId", filter.transactionId()));
        }

        if (specs.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }

        return Specification.allOf(specs);
    }

    private static <V> Specification<AuditEventJpaEntity> equal(String attribute, V value) {
        Objects.requireNonNull(value);
        return (root, query, cb) -> cb.equal(root.get(attribute), value);
    }
}
