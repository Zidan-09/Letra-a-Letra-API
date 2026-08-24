package com.letraaletra.api.features.ticket.domain;

import com.letraaletra.api.features.ticket.domain.exception.InvalidTicketContentException;
import com.letraaletra.api.features.ticket.domain.exception.InvalidTicketStatusException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TicketTest {

    private final UUID ownerId = UUID.randomUUID();

    @Nested
    @DisplayName("Creation")
    class Creation {

        @Test
        @DisplayName("Should create ticket with PENDING status, generated id and preserved content")
        void shouldCreatePendingTicket() {
            Ticket ticket = Ticket.create(ownerId, TicketCategory.BUG, "Cannot login", "The game crashes when I try to login");

            assertNotNull(ticket.getTicketId());
            assertEquals(ownerId, ticket.getUserId());
            assertEquals(TicketCategory.BUG, ticket.getCategory());
            assertEquals(TicketStatus.PENDING, ticket.getStatus());
            assertEquals("Cannot login", ticket.getSubject());
            assertEquals("The game crashes when I try to login", ticket.getDescription());
            assertNotNull(ticket.getCreatedAt());
            assertNull(ticket.getResolutionNote());
            assertNull(ticket.getResolvedByAdminId());
            assertNull(ticket.getResolvedAt());
        }

        @Test
        @DisplayName("Should generate a distinct UUID for each created ticket")
        void shouldGenerateDistinctIds() {
            Ticket first = Ticket.create(ownerId, TicketCategory.BUG, "Cannot login", "The game crashes when I try to login");
            Ticket second = Ticket.create(ownerId, TicketCategory.FEEDBACK, "Great update", "Loved the new ranking season rewards");

            assertNotEquals(first.getTicketId(), second.getTicketId());
        }

        @Test
        @DisplayName("Should trim subject and description on creation")
        void shouldTrimContent() {
            Ticket ticket = Ticket.create(ownerId, TicketCategory.OTHER, "  Cannot login  ", "  The game crashes when I try to login  ");

            assertEquals("Cannot login", ticket.getSubject());
            assertEquals("The game crashes when I try to login", ticket.getDescription());
        }

        @Test
        @DisplayName("Should reject null owner or category")
        void shouldRejectNullOwnerOrCategory() {
            assertThrows(InvalidTicketContentException.class,
                    () -> Ticket.create(null, TicketCategory.BUG, "Cannot login", "The game crashes when I try to login"));
            assertThrows(InvalidTicketContentException.class,
                    () -> Ticket.create(ownerId, null, "Cannot login", "The game crashes when I try to login"));
        }

        @Test
        @DisplayName("Should reject blank subject")
        void shouldRejectBlankSubject() {
            assertThrows(InvalidTicketContentException.class,
                    () -> Ticket.create(ownerId, TicketCategory.BUG, "   ", "The game crashes when I try to login"));
        }

        @Test
        @DisplayName("Should reject subject outside length limits")
        void shouldRejectSubjectLengthOutOfBounds() {
            assertThrows(InvalidTicketContentException.class,
                    () -> Ticket.create(ownerId, TicketCategory.BUG, "tiny", "The game crashes when I try to login"));
            assertThrows(InvalidTicketContentException.class,
                    () -> Ticket.create(ownerId, TicketCategory.BUG, "s".repeat(101), "The game crashes when I try to login"));
        }

        @Test
        @DisplayName("Should accept subject at exact boundaries")
        void shouldAcceptSubjectAtBoundaries() {
            Ticket min = Ticket.create(ownerId, TicketCategory.BUG, "abcde", "The game crashes when I try to login");
            Ticket max = Ticket.create(ownerId, TicketCategory.BUG, "s".repeat(100), "The game crashes when I try to login");

            assertEquals(5, min.getSubject().length());
            assertEquals(100, max.getSubject().length());
        }

        @Test
        @DisplayName("Should reject blank or out of bounds description")
        void shouldRejectInvalidDescription() {
            assertThrows(InvalidTicketContentException.class,
                    () -> Ticket.create(ownerId, TicketCategory.BUG, "Cannot login", " "));
            assertThrows(InvalidTicketContentException.class,
                    () -> Ticket.create(ownerId, TicketCategory.BUG, "Cannot login", "too short"));
            assertThrows(InvalidTicketContentException.class,
                    () -> Ticket.create(ownerId, TicketCategory.BUG, "Cannot login", "d".repeat(4001)));
        }
    }

    @Nested
    @DisplayName("Resolution")
    class Resolution {

        @Test
        @DisplayName("Should resolve pending ticket recording admin, date and note")
        void shouldResolvePendingTicket() {
            Ticket ticket = Ticket.create(ownerId, TicketCategory.SUGGESTION, "Add dark mode", "Please add a dark mode option to the settings screen");
            UUID adminId = UUID.randomUUID();

            ticket.resolve(adminId, " Implemented in next release ");

            assertEquals(TicketStatus.RESOLVED, ticket.getStatus());
            assertEquals(adminId, ticket.getResolvedByAdminId());
            assertNotNull(ticket.getResolvedAt());
            assertEquals("Implemented in next release", ticket.getResolutionNote());
        }

        @Test
        @DisplayName("Should resolve without note keeping it null")
        void shouldResolveWithoutNote() {
            Ticket ticket = Ticket.create(ownerId, TicketCategory.FEEDBACK, "Great update", "Loved the new ranking season rewards");
            UUID adminId = UUID.randomUUID();

            ticket.resolve(adminId, null);

            assertEquals(TicketStatus.RESOLVED, ticket.getStatus());
            assertNull(ticket.getResolutionNote());
        }

        @Test
        @DisplayName("Should reject resolving an already resolved ticket")
        void shouldRejectSecondResolution() {
            Ticket ticket = Ticket.create(ownerId, TicketCategory.BUG, "Cannot login", "The game crashes when I try to login");
            ticket.resolve(UUID.randomUUID(), null);

            assertThrows(InvalidTicketStatusException.class, () -> ticket.resolve(UUID.randomUUID(), "again"));
        }

        @Test
        @DisplayName("Should reject resolution without admin identity")
        void shouldRejectResolutionWithoutAdmin() {
            Ticket ticket = Ticket.create(ownerId, TicketCategory.BUG, "Cannot login", "The game crashes when I try to login");

            assertThrows(InvalidTicketContentException.class, () -> ticket.resolve(null, null));
            assertEquals(TicketStatus.PENDING, ticket.getStatus());
        }

        @Test
        @DisplayName("Should reject oversized resolution note")
        void shouldRejectOversizedNote() {
            Ticket ticket = Ticket.create(ownerId, TicketCategory.BUG, "Cannot login", "The game crashes when I try to login");

            assertThrows(InvalidTicketContentException.class, () -> ticket.resolve(UUID.randomUUID(), "n".repeat(1001)));
            assertEquals(TicketStatus.PENDING, ticket.getStatus());
        }
    }

    @Nested
    @DisplayName("Ownership")
    class Ownership {

        @Test
        @DisplayName("belongsTo should return true only for the owner")
        void belongsToShouldMatchOnlyOwner() {
            Ticket ticket = Ticket.create(ownerId, TicketCategory.OTHER, "General doubt", "How does the matchmaking rating work exactly?");

            assertTrue(ticket.belongsTo(ownerId));
            assertFalse(ticket.belongsTo(UUID.randomUUID()));
        }
    }
}
