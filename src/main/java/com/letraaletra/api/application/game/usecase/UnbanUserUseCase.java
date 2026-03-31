package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.ModerationContext;
import com.letraaletra.api.application.game.service.ResolveModerationContext;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnbanUserUseCase {
    @Autowired
    private ResolveModerationContext resolveModerationContext;

    @Autowired
    private GameRepository gameRepository;

    public void execute(String tokenGameId, String participantId, String userId) {
        ModerationContext context = resolveModerationContext.resolve(tokenGameId, participantId, userId);
        Game game = context.game();

        game.removeFromBlackList(participantId);

        gameRepository.save(game);
    }
}
