package com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketDetails;
import com.letraaletra.api.features.ticket.domain.TicketFilter;
import com.letraaletra.api.features.ticket.domain.TicketsPage;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.jpa.SpringDataTicketRepository;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.mapper.TicketMapper;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.projection.TicketProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTicketRepository implements TicketRepository {

    private static final String CREATED_AT_FIELD = "createdAt";

    private final SpringDataTicketRepository repository;

    public JpaTicketRepository(SpringDataTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Ticket ticket) {
        repository.save(TicketMapper.toEntity(ticket));
    }

    @Override
    public Optional<TicketDetails> findDetailsById(UUID ticketId) {
        return repository.findByIdDetails(ticketId)
                .map(TicketMapper::toDetails);
    }

    @Override
    public Optional<Ticket> findById(UUID ticketId) {
        return repository.findById(ticketId)
                .map(TicketMapper::toDomain);
    }

    @Override
    public Page<TicketDetails> findUserTickets(UUID userId, TicketsPage page) {
        Pageable pageable = toPageable(page);

        return repository.findByUserId(userId, pageable)
                .map(TicketMapper::toDetails);
    }

    @Override
    public Page<TicketDetails> findTickets(TicketFilter filter, TicketsPage page) {
        Pageable pageable = toPageable(page);

        UUID userId = filter.userId();
        String category = filter.category() != null ? filter.category().name() : null;
        String status = filter.status() != null ? filter.status().name() : null;

        Page<TicketProjection> projections = resolveProjections(userId, category, status, pageable);

        return projections.map(TicketMapper::toDetails);
    }

    @Override
    public Page<TicketDetails> findByUsername(String username, TicketsPage page) {
        Pageable pageable = toPageable(page);

        return repository.findByUsername(username, pageable)
                .map(TicketMapper::toDetails);
    }

    private Page<TicketProjection> resolveProjections(
            UUID userId, String category, String status, Pageable pageable
    ) {
        boolean hasCategory = category != null;
        boolean hasStatus = status != null;
        boolean hasUserId = userId != null;

        if (hasCategory && hasStatus && hasUserId) {
            return repository.findByCategoryStatusUserId(category, status, userId, pageable);
        }
        if (hasCategory && hasUserId) {
            return repository.findByCategoryUserId(category, userId, pageable);
        }
        if (hasStatus && hasUserId) {
            return repository.findByStatusUserId(status, userId, pageable);
        }
        if (hasUserId) {
            return repository.findByUserId(userId, pageable);
        }
        if (hasCategory && hasStatus) {
            return repository.findByCategoryStatus(category, status, pageable);
        }
        if (hasCategory) {
            return repository.findByCategory(category, pageable);
        }
        if (hasStatus) {
            return repository.findByStatus(status, pageable);
        }
        return repository.findAllDetails(pageable);
    }

    private Pageable toPageable(TicketsPage page) {
        Sort sort = page.sort() != null ? page.sort() : Sort.by(Sort.Direction.ASC, CREATED_AT_FIELD);
        return org.springframework.data.domain.PageRequest.of(page.page(), page.size(), sort);
    }
}