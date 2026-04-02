package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.KickParticipantCommand;
import com.letraaletra.api.application.context.ModerationContext;
import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.output.participant.KickParticipantOutput;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;

public class KickParticipantUseCase {
    private final GameRepository gameRepository;
    private final ModerationContextFactory moderationContextFactory;

    public KickParticipantUseCase(GameRepository gameRepository, ModerationContextFactory moderationContextFactory) {
        this.gameRepository = gameRepository;
        this.moderationContextFactory = moderationContextFactory;
    }

    public KickParticipantOutput execute(KickParticipantCommand command) {
        ModerationContext context = moderationContextFactory.resolve(
                command.token(),
                command.target(),
                command.user()
        );

        Game game = context.game();

        game.remove(command.target());

        gameRepository.save(game);

        return buildReturn(game, command.token());
    }

    private KickParticipantOutput buildReturn(Game game, String token) {
        return new KickParticipantOutput(
                token,
                game
        );
    }
}
