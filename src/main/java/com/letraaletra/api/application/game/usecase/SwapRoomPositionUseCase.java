package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.user.service.TokenService;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.infra.websocket.BroadcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SwapRoomPositionUseCase {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private BroadcastService broadcast;

    public void execute(String tokenGameId) {
        String gameId = tokenService.getTokenContent(tokenGameId);

        Game game = gameRepository.find(gameId);


    }
}
