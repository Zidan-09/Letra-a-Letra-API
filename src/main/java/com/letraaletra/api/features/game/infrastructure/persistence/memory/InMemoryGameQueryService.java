package com.letraaletra.api.features.game.infrastructure.persistence.memory;

import com.letraaletra.api.features.game.application.input.FindActiveGameByRoomNameInput;
import com.letraaletra.api.features.game.application.input.GetActiveGamesInput;
import com.letraaletra.api.features.game.application.input.GetPublicGamesInput;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.game.application.port.GameQueryService;
import com.letraaletra.api.features.game.domain.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class InMemoryGameQueryService implements GameQueryService {
    private final ActorManager<Game> actorManager;

    public InMemoryGameQueryService(ActorManager<Game> actorManager) {
        this.actorManager = actorManager;
    }

    @Override
    public boolean existsByCode(String code) {
        return getGames()
                .anyMatch(game -> game.getCode().equals(code));
    }

    @Override
    public Optional<Game> findByCode(String code) {
        return getGames()
                .filter(game -> game.getCode().equals(code))
                .findFirst();
    }

    @Override
    public Page<Game> getPublic(GetPublicGamesInput input) {
        List<Game> games = getGames()
                .filter(game -> !game.getRoomSettings().isPrivateGame())
                .toList();

        Pageable pageable = PageRequest.of(
                input.page(),
                input.size(),
                input.sort()
        );

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), games.size());

        List<Game> content = start >= games.size()
                ? List.of()
                : games.subList(start, end);

        return new PageImpl<>(content, pageable, games.size());
    }

    @Override
    public Page<Game> getAllActiveGames(GetActiveGamesInput input) {
        List<Game> games = getGames().toList();

        Pageable pageable = PageRequest.of(
                input.page(),
                input.size(),
                input.sort()
        );

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), games.size());

        List<Game> content = start >= games.size()
                ? List.of()
                : games.subList(start, end);

        return new PageImpl<>(content, pageable, games.size());
    }

    @Override
    public Page<Game> searchActiveByRoomName(FindActiveGameByRoomNameInput input) {
        String search = input.roomName() == null ? "" : input.roomName().toLowerCase();

        List<Game> games = getGames()
                .filter(game -> game.getRoomName() != null
                        && game.getRoomName().toLowerCase().contains(search))
                .sorted((left, right) -> {
                    boolean leftPrefix = left.getRoomName().toLowerCase().startsWith(search);
                    boolean rightPrefix = right.getRoomName().toLowerCase().startsWith(search);
                    if (leftPrefix != rightPrefix) {
                        return leftPrefix ? -1 : 1;
                    }
                    return left.getRoomName().compareToIgnoreCase(right.getRoomName());
                })
                .toList();

        Pageable pageable = PageRequest.of(
                input.page(),
                input.size(),
                input.sort()
        );

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), games.size());

        List<Game> content = start >= games.size()
                ? List.of()
                : games.subList(start, end);

        return new PageImpl<>(content, pageable, games.size());
    }

    private Stream<Game> getGames() {
        return actorManager.getAllActors().stream()
                .map(Actor::getGame);
    }
}
