package com.letraaletra.api.infrastructure.config;

import com.letraaletra.api.application.port.GameTimeOut;
import com.letraaletra.api.application.usecase.game.*;
import com.letraaletra.api.domain.game.board.service.BoardGenerator;
import com.letraaletra.api.domain.game.service.GameStateGenerator;
import com.letraaletra.api.domain.game.service.GenerateRoomCode;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.ThemeRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.security.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class GameUseCaseConfig {
    @Bean
    public CloseRoomDueToTimeoutUseCase closeRoomDueToTimeoutUseCase(GameRepository gameRepository, UserRepository userRepository) {
        return new CloseRoomDueToTimeoutUseCase(gameRepository, userRepository);
    }

    @Bean
    public CreateGameUseCase createGameUseCase(UserRepository userRepository, GameRepository gameRepository, GameTimeOut gameTimeOut, TokenService tokenService, GenerateRoomCode generateRoomCode) {
        return new CreateGameUseCase(userRepository, gameRepository, gameTimeOut, tokenService, generateRoomCode);
    }

    @Bean
    public FindByCodeUseCase findByCodeUseCase(GameRepository gameRepository, TokenService tokenService) {
        return new FindByCodeUseCase(gameRepository, tokenService);
    }

    @Bean
    public FindByTokenGameIdUseCase findByTokenGameIdUseCase(TokenService tokenService, GameRepository gameRepository) {
        return new FindByTokenGameIdUseCase(tokenService, gameRepository);
    }

    @Bean
    public GetPublicGamesUseCase getPublicGamesUseCase(GameRepository gameRepository, TokenService tokenService) {
        return new GetPublicGamesUseCase(gameRepository, tokenService);
    }

    @Bean
    public JoinGameUseCase joinGameUseCase(GameRepository gameRepository, UserRepository userRepository, TokenService tokenService) {
        return new JoinGameUseCase(gameRepository, userRepository, tokenService);
    }

    @Bean
    public LeftGameUseCase leftGameUseCase(GameRepository gameRepository, UserRepository userRepository, TokenService tokenService) {
        return new LeftGameUseCase(gameRepository, userRepository, tokenService);
    }

    @Bean
    public PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase(ThemeRepository repository, Random random) {
        return new PickRandomThemeWordsUseCase(repository, random);
    }

    @Bean
    public StartGameUseCase startGameUseCase(GameRepository gameRepository,
                                             GameStateGenerator gameStateGenerator,
                                             ThemeRepository themeRepository,
                                             GameTimeOut gameTimeOut,
                                             PickRandomThemeWordsUseCase pickRandomThemeWordsUseCase,
                                             BoardGenerator boardGenerator,
                                             TokenService tokenService
    ) {
        return new StartGameUseCase(
                gameRepository,
                gameStateGenerator,
                themeRepository,
                gameTimeOut,
                pickRandomThemeWordsUseCase,
                boardGenerator,
                tokenService
        );
    }
}
