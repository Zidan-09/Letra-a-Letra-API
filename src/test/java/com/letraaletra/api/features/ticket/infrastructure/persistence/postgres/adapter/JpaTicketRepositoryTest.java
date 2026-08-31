package com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.ticket.domain.*;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.entity.TicketJpaEntity;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.jpa.SpringDataTicketRepository;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.mapper.TicketMapper;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.projection.TicketProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaTicketRepositoryTest {

    @Mock
    private SpringDataTicketRepository springDataTicketRepository;

    private JpaTicketRepository jpaTicketRepository;

    @BeforeEach
    void setUp() {
        jpaTicketRepository = new JpaTicketRepository(springDataTicketRepository);
    }

    private Ticket buildTicket() {
        return Ticket.create(
                UUID.randomUUID(),
                TicketCategory.BUG,
                "Cannot login",
                "The game crashes when I try to login"
        );
    }

    @Test
    @DisplayName("save should map every domain field onto the JPA entity")
    void saveShouldMapDomainToEntity() {
        Ticket ticket = buildTicket();

        jpaTicketRepository.save(ticket);

        ArgumentCaptor<TicketJpaEntity> captor = ArgumentCaptor.forClass(TicketJpaEntity.class);
        verify(springDataTicketRepository).save(captor.capture());

        TicketJpaEntity entity = captor.getValue();
        assertEquals(ticket.getTicketId(), entity.getId());
        assertEquals(ticket.getUserId(), entity.getUserId());
        assertEquals(TicketCategory.BUG, entity.getCategory());
        assertEquals(TicketStatus.PENDING, entity.getStatus());
        assertEquals("Cannot login", entity.getSubject());
        assertEquals(ticket.getCreatedAt(), entity.getCreatedAt());
    }

    @Test
    @DisplayName("findById should map the entity back to the domain aggregate")
    void findByIdShouldMapEntityToDomain() {
        Ticket ticket = buildTicket();
        when(springDataTicketRepository.findById(ticket.getTicketId()))
                .thenReturn(Optional.of(TicketMapper.toEntity(ticket)));

        Optional<Ticket> result = jpaTicketRepository.findById(ticket.getTicketId());

        assertTrue(result.isPresent());
        assertEquals(ticket.getTicketId(), result.get().getTicketId());
        assertEquals(ticket.getUserId(), result.get().getUserId());
        assertEquals(TicketStatus.PENDING, result.get().getStatus());
    }

    @Test
    @DisplayName("findDetailsById should map the projection back to the domain details")
    void findDetailsByIdShouldMapProjectionToDetails() {
        Ticket ticket = buildTicket();
        TicketProjection projection = TicketMapper.toProjection(ticket);
        when(springDataTicketRepository.findByIdDetails(ticket.getTicketId()))
                .thenReturn(Optional.of(projection));

        Optional<TicketDetails> result = jpaTicketRepository.findDetailsById(ticket.getTicketId());

        assertTrue(result.isPresent());
        assertEquals(ticket.getTicketId(), result.get().ticketId());
        assertEquals(ticket.getUserId(), result.get().userId());
        assertEquals(ticket.getCategory(), result.get().category());
        assertEquals(ticket.getStatus(), result.get().status());
        assertEquals(ticket.getSubject(), result.get().subject());
        assertEquals(ticket.getDescription(), result.get().description());
        assertEquals(ticket.getCreatedAt(), result.get().createdAt());
    }

    @Test
    @DisplayName("findUserTickets should page by user with createdAt sorting")
    void findUserTicketsShouldPageByUser() {
        when(springDataTicketRepository.findByUserId(any(UUID.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        TicketsPage ticketsPage = new TicketsPage(2, 25, null);
        jpaTicketRepository.findUserTickets(UUID.randomUUID(), ticketsPage);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(springDataTicketRepository).findByUserId(any(UUID.class), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(2, pageable.getPageNumber());
        assertEquals(25, pageable.getPageSize());
    }

    @Test
    @DisplayName("findTickets should apply ascending ordering when requested")
    void findTicketsShouldApplyAscendingOrdering() {
        when(springDataTicketRepository.findByCategoryUserId(anyString(), any(UUID.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        TicketsPage ticketsPage = new TicketsPage(0, 10, Sort.by(Sort.Direction.ASC, "createdAt"));
        jpaTicketRepository.findTickets(new TicketFilter(null, TicketCategory.BUG, UUID.randomUUID()), ticketsPage);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(springDataTicketRepository).findByCategoryUserId(anyString(), any(UUID.class), pageableCaptor.capture());

        assertEquals(Sort.by(Sort.Direction.ASC, "createdAt"), pageableCaptor.getValue().getSort());
    }

    @Test
    @DisplayName("findTickets should map paged entities back to the domain")
    void findTicketsShouldMapPagedContent() {
        Ticket ticket = buildTicket();
        TicketProjection projection = TicketMapper.toProjection(ticket);
        Page<TicketProjection> projectionPage = new PageImpl<>(List.of(projection));
        when(springDataTicketRepository.findByCategoryUserId(anyString(), any(UUID.class), any(Pageable.class)))
                .thenReturn(projectionPage);

        TicketsPage ticketsPage = new TicketsPage(0, 20, null);
        Page<TicketDetails> result = jpaTicketRepository.findTickets(
                new TicketFilter(null, TicketCategory.BUG, ticket.getUserId()),
                ticketsPage
        );

        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        assertEquals(ticket.getTicketId(), result.getContent().getFirst().ticketId());
        assertEquals(ticket.getUserId(), result.getContent().getFirst().userId());
        assertEquals(ticket.getCategory(), result.getContent().getFirst().category());
        assertEquals(ticket.getSubject(), result.getContent().getFirst().subject());
        assertEquals(ticket.getDescription(), result.getContent().getFirst().description());
    }

    @Test
    @DisplayName("findByUsername should map paged entities back to the domain")
    void findByUsernameShouldMapPagedContent() {
        Ticket ticket = buildTicket();
        TicketProjection projection = TicketMapper.toProjection(ticket, "testuser", null);
        Page<TicketProjection> projectionPage = new PageImpl<>(List.of(projection));
        when(springDataTicketRepository.findByUsername(anyString(), any(Pageable.class)))
                .thenReturn(projectionPage);

        TicketsPage ticketsPage = new TicketsPage(0, 20, null);
        Page<TicketDetails> result = jpaTicketRepository.findByUsername("testuser", ticketsPage);

        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        assertEquals(ticket.getTicketId(), result.getContent().getFirst().ticketId());
        assertEquals(ticket.getUserId(), result.getContent().getFirst().userId());
        assertEquals(ticket.getCategory(), result.getContent().getFirst().category());
        assertEquals(ticket.getSubject(), result.getContent().getFirst().subject());
        assertEquals(ticket.getDescription(), result.getContent().getFirst().description());
        assertEquals("testuser", result.getContent().getFirst().username());
    }
}