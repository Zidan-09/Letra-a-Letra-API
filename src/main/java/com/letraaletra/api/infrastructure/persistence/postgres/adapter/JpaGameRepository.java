package com.letraaletra.api.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.infrastructure.persistence.postgres.jpa.SpringDataGameRepository;
import com.letraaletra.api.infrastructure.persistence.postgres.mapper.GameMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaGameRepository implements GameRepository {
    private final SpringDataGameRepository repository;

    public JpaGameRepository(SpringDataGameRepository repository) {
        this.repository = repository;
    }

    @Override
    public Game save(Game game) {
        repository.save(GameMapper.toEntity(game));
        return game;
    }

    @Override
    public Optional<Game> find(String id) {
        return repository.findById(UUID.fromString(id))
                .map(GameMapper::toDomain);
    }
}
