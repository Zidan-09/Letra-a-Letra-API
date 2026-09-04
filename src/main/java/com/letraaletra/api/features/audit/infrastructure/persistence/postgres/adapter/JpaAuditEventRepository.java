package com.letraaletra.api.features.audit.infrastructure.persistence.postgres.adapter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventDetails;
import com.letraaletra.api.features.audit.domain.AuditEventFilter;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditOutcome;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.audit.domain.repository.FindAuditEvents;
import com.letraaletra.api.features.audit.domain.repository.SaveAuditEvent;
import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.jpa.SpringDataAuditEventRepository;
import com.letraaletra.api.features.audit.infrastructure.persistence.postgres.mapper.AuditEventJpaMapper;

@Repository
public class JpaAuditEventRepository implements SaveAuditEvent, FindAuditEvents {

    private final SpringDataAuditEventRepository repository;
    private final EntityManager entityManager;

    public JpaAuditEventRepository(SpringDataAuditEventRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    public void save(AuditEvent event) {
        repository.save(AuditEventJpaMapper.toEntity(event));
    }

    @Override
    public Page<AuditEventDetails> find(AuditEventFilter filter, int page, int size, boolean ascending) {
        Sort sort = Sort.by(
                ascending ? Sort.Direction.ASC : Sort.Direction.DESC,
                "occurredAt",
                "eventId"
        );

        PageRequest pageable = PageRequest.of(page, size, sort);

        List<String> conditions = new ArrayList<>();
        StringBuilder dataJpql = new StringBuilder(SpringDataAuditEventRepository.FIND_DETAILS);
        StringBuilder countJpql = new StringBuilder("SELECT COUNT(a) FROM AuditEventJpaEntity a");

        String resourceId = blankToNull(filter.resourceId());
        String requestId = blankToNull(filter.requestId());
        String correlationId = blankToNull(filter.correlationId());

        if (filter.targetUserId() != null) conditions.add("a.targetUserId = :targetUserId");
        if (filter.actorId() != null) conditions.add("a.actorId = :actorId");
        if (filter.eventType() != null) conditions.add("a.eventType = :eventType");
        if (filter.category() != null) conditions.add("a.category = :category");
        if (filter.outcome() != null) conditions.add("a.outcome = :outcome");
        if (filter.resourceType() != null) conditions.add("a.resourceType = :resourceType");
        if (resourceId != null) conditions.add("a.resourceId = :resourceId");
        if (filter.from() != null) conditions.add("a.occurredAt >= :from");
        if (filter.to() != null) conditions.add("a.occurredAt <= :to");
        if (requestId != null) conditions.add("a.requestId = :requestId");
        if (filter.operationId() != null) conditions.add("a.operationId = :operationId");
        if (correlationId != null) conditions.add("a.correlationId = :correlationId");
        if (filter.transactionId() != null) conditions.add("a.transactionId = :transactionId");

        if (!conditions.isEmpty()) {
            String whereClause = " WHERE " + String.join(" AND ", conditions);
            dataJpql.append(whereClause);
            countJpql.append(whereClause);
        }

        dataJpql.append(ascending ? " ORDER BY a.occurredAt ASC, a.eventId ASC"
                : " ORDER BY a.occurredAt DESC, a.eventId DESC");

        TypedQuery<Tuple> query = entityManager.createQuery(dataJpql.toString(), Tuple.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql.toString(), Long.class);

        if (filter.targetUserId() != null) {
            query.setParameter("targetUserId", filter.targetUserId());
            countQuery.setParameter("targetUserId", filter.targetUserId());
        }
        if (filter.actorId() != null) {
            query.setParameter("actorId", filter.actorId());
            countQuery.setParameter("actorId", filter.actorId());
        }
        if (filter.eventType() != null) {
            query.setParameter("eventType", filter.eventType());
            countQuery.setParameter("eventType", filter.eventType());
        }
        if (filter.category() != null) {
            query.setParameter("category", filter.category());
            countQuery.setParameter("category", filter.category());
        }
        if (filter.outcome() != null) {
            query.setParameter("outcome", filter.outcome());
            countQuery.setParameter("outcome", filter.outcome());
        }
        if (filter.resourceType() != null) {
            query.setParameter("resourceType", filter.resourceType());
            countQuery.setParameter("resourceType", filter.resourceType());
        }
        if (resourceId != null) {
            query.setParameter("resourceId", resourceId);
            countQuery.setParameter("resourceId", resourceId);
        }
        if (filter.from() != null) {
            query.setParameter("from", filter.from());
            countQuery.setParameter("from", filter.from());
        }
        if (filter.to() != null) {
            query.setParameter("to", filter.to());
            countQuery.setParameter("to", filter.to());
        }
        if (requestId != null) {
            query.setParameter("requestId", requestId);
            countQuery.setParameter("requestId", requestId);
        }
        if (filter.operationId() != null) {
            query.setParameter("operationId", filter.operationId());
            countQuery.setParameter("operationId", filter.operationId());
        }
        if (correlationId != null) {
            query.setParameter("correlationId", correlationId);
            countQuery.setParameter("correlationId", correlationId);
        }
        if (filter.transactionId() != null) {
            query.setParameter("transactionId", filter.transactionId());
            countQuery.setParameter("transactionId", filter.transactionId());
        }

        query.setFirstResult(page * size);
        query.setMaxResults(size);

        List<Tuple> tuples = query.getResultList();
        List<AuditEventDetails> content = tuples.stream().map(this::toDetails).toList();

        Long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public boolean existsByTransactionId(UUID transactionId) {
        return repository.existsByTransactionId(transactionId);
    }

    @SuppressWarnings("unchecked")
    private AuditEventDetails toDetails(Tuple tuple) {
        UUID eventId = tuple.get("eventId", UUID.class);
        Instant occurredAt = tuple.get("occurredAt", Instant.class);
        AuditCategory category = tuple.get("category", AuditCategory.class);
        AuditEventType eventType = tuple.get("eventType", AuditEventType.class);
        AuditOutcome outcome = tuple.get("outcome", AuditOutcome.class);
        String failureReason = tuple.get("failureReason", String.class);
        AuditActorType actorType = tuple.get("actorType", AuditActorType.class);
        UUID actorId = tuple.get("actorId", UUID.class);
        String actorName = tuple.get("actorName", String.class);
        UUID targetUserId = tuple.get("targetUserId", UUID.class);
        String targetUsername = tuple.get("targetUsername", String.class);
        AuditResourceType resourceType = tuple.get("resourceType", AuditResourceType.class);
        String resourceId = tuple.get("resourceId", String.class);
        Map<String, Object> beforeState = tuple.get("beforeState", Map.class);
        Map<String, Object> afterState = tuple.get("afterState", Map.class);
        Map<String, Object> delta = tuple.get("delta", Map.class);
        String reasonCode = tuple.get("reasonCode", String.class);
        String requestId = tuple.get("requestId", String.class);
        UUID operationId = tuple.get("operationId", UUID.class);
        String correlationId = tuple.get("correlationId", String.class);
        AuditSourceType sourceType = tuple.get("sourceType", AuditSourceType.class);
        String sourceDetail = tuple.get("sourceDetail", String.class);
        UUID transactionId = tuple.get("transactionId", UUID.class);
        Map<String, Object> metadata = tuple.get("metadata", Map.class);

        AuditActor actor = new AuditActor(actorType, actorId, actorName);
        return new AuditEventDetails(
                eventId, occurredAt, category, eventType, outcome, failureReason,
                actor, targetUserId, targetUsername, resourceType, resourceId,
                beforeState, afterState, delta, reasonCode, requestId, operationId,
                correlationId, sourceType, sourceDetail, transactionId, metadata);
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value;
    }
}
