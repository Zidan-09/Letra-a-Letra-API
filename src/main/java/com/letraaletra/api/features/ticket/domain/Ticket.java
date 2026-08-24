package com.letraaletra.api.features.ticket.domain;

import com.letraaletra.api.features.ticket.domain.exception.InvalidTicketContentException;
import com.letraaletra.api.features.ticket.domain.exception.InvalidTicketStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket {
    private static final int SUBJECT_MIN_LENGTH = 5;
    private static final int SUBJECT_MAX_LENGTH = 100;
    private static final int DESCRIPTION_MIN_LENGTH = 10;
    private static final int DESCRIPTION_MAX_LENGTH = 4000;
    private static final int RESOLUTION_NOTE_MAX_LENGTH = 1000;

    private final UUID ticketId;
    private final UUID userId;
    private final TicketCategory category;
    private final String subject;
    private final String description;
    private TicketStatus status;
    private String resolutionNote;
    private UUID resolvedByAdminId;
    private LocalDateTime resolvedAt;
    private final LocalDateTime createdAt;

    private Ticket(
            UUID ticketId,
            UUID userId,
            TicketCategory category,
            String subject,
            String description,
            TicketStatus status,
            String resolutionNote,
            UUID resolvedByAdminId,
            LocalDateTime resolvedAt,
            LocalDateTime createdAt
    ) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.category = category;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.resolutionNote = resolutionNote;
        this.resolvedByAdminId = resolvedByAdminId;
        this.resolvedAt = resolvedAt;
        this.createdAt = createdAt;
    }

    public static Ticket create(
            UUID userId,
            TicketCategory category,
            String subject,
            String description
    ) {
        if (userId == null || category == null) {
            throw new InvalidTicketContentException();
        }

        validateSubject(subject);
        validateDescription(description);

        return new Ticket(
                UUID.randomUUID(),
                userId,
                category,
                subject.trim(),
                description.trim(),
                TicketStatus.PENDING,
                null,
                null,
                null,
                LocalDateTime.now()
        );
    }

    public static Ticket restore(
            UUID ticketId,
            UUID userId,
            TicketCategory category,
            String subject,
            String description,
            TicketStatus status,
            String resolutionNote,
            UUID resolvedByAdminId,
            LocalDateTime resolvedAt,
            LocalDateTime createdAt
    ) {
        return new Ticket(
                ticketId,
                userId,
                category,
                subject,
                description,
                status,
                resolutionNote,
                resolvedByAdminId,
                resolvedAt,
                createdAt
        );
    }

    private static void validateSubject(String subject) {
        if (subject == null || subject.isBlank()) {
            throw new InvalidTicketContentException();
        }

        String trimmed = subject.trim();

        if (trimmed.length() < SUBJECT_MIN_LENGTH || trimmed.length() > SUBJECT_MAX_LENGTH) {
            throw new InvalidTicketContentException();
        }
    }

    private static void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new InvalidTicketContentException();
        }

        String trimmed = description.trim();

        if (trimmed.length() < DESCRIPTION_MIN_LENGTH || trimmed.length() > DESCRIPTION_MAX_LENGTH) {
            throw new InvalidTicketContentException();
        }
    }

    public void resolve(UUID adminId, String resolutionNote) {
        if (adminId == null) {
            throw new InvalidTicketContentException();
        }

        if (!status.equals(TicketStatus.PENDING)) {
            throw new InvalidTicketStatusException();
        }

        if (resolutionNote != null && resolutionNote.trim().length() > RESOLUTION_NOTE_MAX_LENGTH) {
            throw new InvalidTicketContentException();
        }

        status = TicketStatus.RESOLVED;
        resolvedByAdminId = adminId;
        resolvedAt = LocalDateTime.now();
        this.resolutionNote = resolutionNote == null || resolutionNote.isBlank()
                ? null
                : resolutionNote.trim();
    }

    public boolean belongsTo(UUID userId) {
        return this.userId.equals(userId);
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public UUID getUserId() {
        return userId;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public String getResolutionNote() {
        return resolutionNote;
    }

    public UUID getResolvedByAdminId() {
        return resolvedByAdminId;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
