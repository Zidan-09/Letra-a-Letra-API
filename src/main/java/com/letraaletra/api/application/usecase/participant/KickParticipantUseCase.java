package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.actor.KickParticipantActorCommand;
import com.letraaletra.api.application.command.participant.KickParticipantCommand;
import com.letraaletra.api.application.context.ModerationContext;
import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.output.participant.KickParticipantOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorManager;
import com.letraaletra.api.domain.game.Game;

import java.util.concurrent.CompletableFuture;

public class KickParticipantUseCase {
    private final ModerationContextFactory moderationContextFactory;
    private final ActorManager gameActorManager;

    public KickParticipantUseCase(ModerationContextFactory moderationContextFactory, ActorManager gameActorManager) {
        this.moderationContextFactory = moderationContextFactory;
        this.gameActorManager = gameActorManager;
    }

    public KickParticipantOutput execute(KickParticipantCommand command) {
        ModerationContext context = moderationContextFactory.resolve(
                command.token(),
                command.target(),
                command.user()
        );

        Actor actor = gameActorManager.get(context.game().getId());

        CompletableFuture<Game> future = actor.enqueueCommand(new KickParticipantActorCommand(command.target(), command.user()));
        Game game = future.join();

        return buildReturn(game, command.token());
    }

    private KickParticipantOutput buildReturn(Game game, String token) {
        return new KickParticipantOutput(
                token,
                game
        );
    }
}
