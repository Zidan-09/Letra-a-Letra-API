package com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketCategory;
import com.letraaletra.api.features.ticket.domain.TicketFilter;
import com.letraaletra.api.features.ticket.domain.TicketStatus;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.entity.TicketJpaEntity;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.jpa.SpringDataTicketRepository;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.mapper.TicketMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
    @DisplayName("findUserTickets should page by user with createdAt sorting")
    void findUserTicketsShouldPageByUser() {
        when(springDataTicketRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        jpaTicketRepository.findUserTickets(UUID.randomUUID(), 2, 25, false);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(springDataTicketRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(2, pageable.getPageNumber());
        assertEquals(25, pageable.getPageSize());
        assertEquals(Sort.by(Sort.Direction.DESC, "createdAt"), pageable.getSort());
    }

    @Test
    @DisplayName("findTickets should apply ascending ordering when requested")
    void findTicketsShouldApplyAscendingOrdering() {
        when(springDataTicketRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        jpaTicketRepository.findTickets(new TicketFilter(TicketStatus.RESOLVED, null, null), 0, 10, true);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(springDataTicketRepository).findAll(any(Specification.class), pageableCaptor.capture());

        assertEquals(Sort.by(Sort.Direction.ASC, "createdAt"), pageableCaptor.getValue().getSort());
    }

    @Test
    @DisplayName("findTickets should map paged entities back to the domain")
    void findTicketsShouldMapPagedContent() {
        Ticket ticket = buildTicket();
        TicketJpaEntity entity = TicketMapper.toEntity(ticket);
        Page<TicketJpaEntity> entityPage = new PageImpl<>(List.of(entity));
        when(springDataTicketRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(entityPage);

        Page<Ticket> result = jpaTicketRepository.findTickets(
                new TicketFilter(null, TicketCategory.BUG, ticket.getUserId()),
                0,
                20,
                false
        );

        assertNotNull(result.getContent());
        assertEquals(1, result.getContent().size());
        assertEquals(ticket.getTicketId(), result.getContent().get(0).getTicketId());
    }
}