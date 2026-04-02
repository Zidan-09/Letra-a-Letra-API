package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.BanParticipantCommand;
import com.letraaletra.api.application.context.ModerationContext;
import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.output.participant.BanParticipantOutput;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;

public class BanParticipantUseCase {
    private final GameRepository gameRepository;
    private final ModerationContextFactory moderationContextFactory;

    public BanParticipantUseCase(GameRepository gameRepository, ModerationContextFactory moderationContextFactory) {
        this.gameRepository = gameRepository;
        this.moderationContextFactory = moderationContextFactory;
    }

    public BanParticipantOutput execute(BanParticipantCommand command) {
        ModerationContext context = moderationContextFactory.resolve(command.token(), command.target(), command.user());
        Game game = context.game();

        game.addToBlackList(command.target());

        gameRepository.save(game);

        return buildReturn(game, command.token());
    }

    private BanParticipantOutput buildReturn(Game game, String token) {
        return new BanParticipantOutput(
                token,
                game
        );
    }
}
