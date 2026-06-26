package com.letraaletra.api.features.matchmaking.infrastructure.worker;

import com.letraaletra.api.features.game.application.port.TurnTimeoutManager;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;
import com.letraaletra.api.features.matchmaking.application.service.MatchmakingAssembler;
import com.letraaletra.api.features.matchmaking.domain.repository.MatchmakingRepository;
import com.letraaletra.api.features.matchmaking.infrastructure.websocket.MatchmakingSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MatchmakingWorker {
    private final MatchmakingRepository repository;
    private final MatchmakingAssembler assembler;
    private final MatchmakingSender sender;
    private final TurnTimeoutManager timeoutManager;

    private final Logger logger = LoggerFactory.getLogger(MatchmakingWorker.class);

    public MatchmakingWorker(
            MatchmakingRepository repository,
            MatchmakingAssembler assembler,
            MatchmakingSender sender,
            TurnTimeoutManager timeoutManager
    ) {
        this.repository = repository;
        this.assembler = assembler;
        this.sender = sender;
        this.timeoutManager = timeoutManager;
        startScheduler();
    }

    private void startScheduler() {
        Thread thread = new Thread(this::processLoop);
        thread.setDaemon(true);
        thread.start();
    }

    private void processLoop() {
        while (true) {
            try {
                processQueue();
            } catch (Exception e) {
                logger.error("Error processing matchmaking", e);
            }
        }
    }

    private void processQueue() {
        for (GameMode mode : GameMode.values()) {
            Optional<MatchmakingPair> pair;

            while ((pair = repository.pollPair(mode)).isPresent()) {
                MatchmakingPair matchmakingPair = pair.get();

                Game game = assembler.create(matchmakingPair, mode);

                timeoutManager.start(game);

                sender.notifierPlayers(game);
            }
        }
    }
}
