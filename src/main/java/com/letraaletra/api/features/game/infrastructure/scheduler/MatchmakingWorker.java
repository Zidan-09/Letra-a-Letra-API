package com.letraaletra.api.features.game.infrastructure.scheduler;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.matchmaking.application.port.GameAssemblerService;
import com.letraaletra.api.features.matchmaking.application.port.MatchmakingSenderService;
import com.letraaletra.api.features.matchmaking.domain.MatchmakingPair;
import com.letraaletra.api.shared.application.port.QueuePairProvider;
import com.letraaletra.api.shared.domain.QueueType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MatchmakingWorker {
    private final QueuePairProvider pairProvider;
    private final GameAssemblerService assembler;
    private final MatchmakingSenderService sender;

    private final Logger logger = LoggerFactory.getLogger(MatchmakingWorker.class);

    public MatchmakingWorker(
            QueuePairProvider pairProvider,
            GameAssemblerService assembler,
            MatchmakingSenderService sender
    ) {
        this.pairProvider = pairProvider;
        this.assembler = assembler;
        this.sender = sender;
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
                pairProvider.get().ifPresent(queueMatch ->
                        startGame(queueMatch.pair(), queueMatch.mode(), queueMatch.type()));

            } catch (Exception e) {
                logger.error("Error processing matchmaking", e);
            }
        }
    }

    private void startGame(MatchmakingPair pair, GameMode mode, QueueType type) {
        Game game = assembler.create(pair, mode, type);

        sender.notify(game, type);
    }
}
