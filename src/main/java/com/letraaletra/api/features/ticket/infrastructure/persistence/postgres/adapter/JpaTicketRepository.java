package com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.ticket.domain.Ticket;
import com.letraaletra.api.features.ticket.domain.TicketFilter;
import com.letraaletra.api.features.ticket.domain.TicketsPage;
import com.letraaletra.api.features.ticket.domain.repository.TicketRepository;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.entity.TicketJpaEntity;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.jpa.SpringDataTicketRepository;
import com.letraaletra.api.features.ticket.infrastructure.persistence.postgres.mapper.TicketMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    public Optional<Ticket> findById(UUID ticketId) {
        return repository.findById(ticketId)
                .map(TicketMapper::toDomain);
    }

    @Override
    public Page<Ticket> findUserTickets(UUID userId, TicketsPage page) {
        return findTickets(new TicketFilter(null, null, userId), page);
    }

    @Override
    public Page<Ticket> findTickets(TicketFilter filter, TicketsPage page) {
        Pageable pageable = toPageable(page);

        return repository.findAll(toSpecification(filter), pageable)
                .map(TicketMapper::toDomain);
    }

    @Override
    public Page<Ticket> findByUsername(String username, TicketsPage page) {
        Pageable pageable = toPageable(page);

        return repository.findByUsername(username, pageable)
                .map(TicketMapper::toDomain);
    }

    private Pageable toPageable(TicketsPage page) {
        Sort sort = page.sort() != null ? page.sort() : Sort.by(Sort.Direction.ASC, CREATED_AT_FIELD);
        return org.springframework.data.domain.PageRequest.of(page.page(), page.size(), sort);
    }

    static Specification<TicketJpaEntity> toSpecification(TicketFilter filter) {
        List<Specification<TicketJpaEntity>> specs = new ArrayList<>();

        if (filter.status() != null) {
            specs.add(equal("status", filter.status()));
        }

        if (filter.category() != null) {
            specs.add(equal("category", filter.category()));
        }

        if (filter.userId() != null) {
            specs.add(equal("userId", filter.userId()));
        }

        if (specs.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }

        return Specification.allOf(specs);
    }

    private static <V> Specification<TicketJpaEntity> equal(String attribute, V value) {
        Objects.requireNonNull(value);
        return (root, query, cb) -> cb.equal(root.get(attribute), value);
    }
}
