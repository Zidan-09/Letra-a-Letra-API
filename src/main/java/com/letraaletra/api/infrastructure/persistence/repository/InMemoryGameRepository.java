package com.letraaletra.api.infrastructure.persistence.repository;

import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGameRepository implements GameRepository {
    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final Map<String, String> codeToId = new ConcurrentHashMap<>();

    @Override
    public void save(Game game) {
        if (!codeToId.containsKey(game.getCode())) {
            this.games.put(game.getId(), game);
            this.codeToId.put(game.getCode(), game.getId());
        }
    }

    @Override
    public Game find(String id) {
        return games.get(id);
    }

    @Override
    public Game findByCode(String code) {
        String id = codeToId.get(code);
        return games.get(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return codeToId.containsKey(code);
    }

    @Override
    public List<Game> get() {
        return List.copyOf(games.values());
    }

    @Override
    public List<Game> getPublic() {
        return List.copyOf(games.values().stream()
                .filter(game -> !game.getRoomSettings().isPrivateGame())
                .toList()
        );
    }

    @Override
    public void removeByCode(String code) {
        String id = codeToId.get(code);
        codeToId.remove(code);
        games.remove(id);
    }
}
