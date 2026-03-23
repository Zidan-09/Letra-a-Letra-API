package com.letraaletra.api.service;

import com.letraaletra.api.domain.Game;
import com.letraaletra.api.exception.exceptions.GameNotFoundException;
import com.letraaletra.api.infra.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    public void createGame() {

    }

    public Game findById(String gameId) {
        Game game = gameRepository.find(gameId);

        if (game == null) {
            throw new GameNotFoundException();
        }

        return game;
    }

    public List<Game> getGames() {
        return gameRepository.get();
    }
}
