package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.domain.game.board.service.BoardGenerator;
import com.letraaletra.api.domain.game.board.service.CellFactory;
import com.letraaletra.api.domain.game.service.GameStateGenerator;
import com.letraaletra.api.domain.game.service.GenerateRoomCode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class DomainConfig {

    @Bean
    public BoardGenerator boardGenerator(CellFactory cellFactory) {
        return new BoardGenerator(cellFactory);
    }

    @Bean
    public CellFactory cellFactory(Random random) {
        return new CellFactory(random);
    }

    @Bean
    public GameStateGenerator gameStateGenerator() {
        return new GameStateGenerator();
    }

    @Bean
    public GenerateRoomCode generateRoomCode() {
        return new GenerateRoomCode();
    }
}
