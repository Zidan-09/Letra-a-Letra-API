package com.letraaletra.api.infra.config;

import com.letraaletra.api.domain.board.service.BoardGenerator;
import com.letraaletra.api.domain.game.service.GameOverChecker;
import com.letraaletra.api.domain.game.service.GameStateGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public BoardGenerator boardGenerator() {
        return new BoardGenerator();
    }

    @Bean
    public GameStateGenerator gameStateGenerator() {
        return new GameStateGenerator();
    }

    @Bean
    public GameOverChecker gameOverChecker() {
        return new GameOverChecker();
    }
}
