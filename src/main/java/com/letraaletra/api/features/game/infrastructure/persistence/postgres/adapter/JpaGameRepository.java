package com.letraaletra.api.features.game.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.game.application.input.GetGamesInput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameHistory;
import com.letraaletra.api.features.game.domain.GameStatus;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchJpaEntity;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.entity.MatchPlayersJpaEntity;
import com.letraaletra.api.features.player.domain.Player;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataGameRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataMatchPlayerRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataMatchRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper.GameMapper;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper.MatchMapper;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.mapper.MatchPlayerMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public void save(Game game) {
        repository.save(GameMapper.toEntity(game));

        if (game.getGameState() != null) {
            LocalDateTime endedAt = !game.getGameStatus().equals(GameStatus.RUNNING) ? LocalDateTime.now() : null;

            matchRepository.save(MatchMapper.toEntity(game.getGameState(), game.getId(), endedAt));

            for (Player player : game.getGameState().getPlayers().values()) {
                matchPlayerRepository.save(MatchPlayerMapper.toEntity(player, game.getGameState().getMatchId()));
            }
        }
    }

    @Override
    public Page<GameHistory> get(GetGamesInput input) {
        Pageable pageable = PageRequest.of(
                input.page(),
                input.size(),
                input.sort()
        );

        return repository.findAll(pageable)
                .map(game -> {

                    List<MatchJpaEntity> matches =
                            matchRepository.findByGameId(game.getId());

                    Map<UUID, List<MatchPlayersJpaEntity>> playersByMatch =
                            matchPlayerRepository.findByMatchPlayerIdMatchIdIn(
                                            matches.stream()
                                                    .map(MatchJpaEntity::getId)
                                                    .toList()
                                    )
                                    .stream()
                                    .collect(Collectors.groupingBy(
                                            player -> player.getMatchPlayerId().getMatchId()
                                    ));

                    return GameMapper.toDomain(
                            game,
                            matches,
                            playersByMatch
                    );
                });
    }
}
