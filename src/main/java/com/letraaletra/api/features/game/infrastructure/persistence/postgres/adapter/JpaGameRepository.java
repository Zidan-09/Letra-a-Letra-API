package com.letraaletra.api.features.game.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataGameRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataMatchPlayerRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataMatchRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper.GameMapper;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper.MatchMapper;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper.MatchPlayerMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class JpaGameRepository implements GameRepository {
    private final SpringDataGameRepository repository;
    private final SpringDataMatchRepository matchRepository;
    private final SpringDataMatchPlayerRepository matchPlayerRepository;

    public JpaGameRepository(
            SpringDataGameRepository repository,
            SpringDataMatchRepository matchRepository,
            SpringDataMatchPlayerRepository matchPlayerRepository
    ) {
        this.repository = repository;
        this.matchRepository = matchRepository;
        this.matchPlayerRepository = matchPlayerRepository;
    }

    @Override
    public Game save(Game game) {
        repository.save(GameMapper.toEntity(game));

        if (game.getGameState() == null) return game;

        LocalDateTime endedAt = !game.getGameStatus().equals(GameStatus.RUNNING) ? LocalDateTime.now() : null;

        matchRepository.save(MatchMapper.toEntity(game.getGameState(), game.getId(), endedAt));

        for (Player player : game.getGameState().getPlayers().values()) {
            matchPlayerRepository.save(MatchPlayerMapper.toEntity(player, game.getGameState().getMatchId()));
        }

        return game;
    }
}
