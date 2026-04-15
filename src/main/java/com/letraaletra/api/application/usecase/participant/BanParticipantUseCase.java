package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.actor.BanParticipantActorCommand;
import com.letraaletra.api.application.command.participant.BanParticipantCommand;
import com.letraaletra.api.application.context.ModerationContext;
import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.output.participant.BanParticipantOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.application.port.ActorRepository;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;

import java.util.concurrent.CompletableFuture;

public class BanParticipantUseCase {
    private final ModerationContextFactory moderationContextFactory;
    private final UserRepository userRepository;
    private final ActorRepository gameActorRepository;

    public BanParticipantUseCase(ModerationContextFactory moderationContextFactory, UserRepository userRepository, ActorRepository gameActorRepository) {
        this.moderationContextFactory = moderationContextFactory;
        this.userRepository = userRepository;
        this.gameActorRepository = gameActorRepository;
    }

    public BanParticipantOutput execute(BanParticipantCommand command) {
        ModerationContext context = moderationContextFactory.resolve(command.token(), command.target(), command.user());

        Actor actor = gameActorRepository.getOrCreate(context.game().getId());
        CompletableFuture<Game> future = actor.enqueueCommand(new BanParticipantActorCommand(command.target(), command.user()));
        Game game = future.join();

        User user = userRepository.find(command.target());
        user.leaveGame();

        userRepository.save(user);

        return buildReturn(game, command.token());
    }

    private BanParticipantOutput buildReturn(Game game, String token) {
        return new BanParticipantOutput(
                token,
                game
        );
    }
}
