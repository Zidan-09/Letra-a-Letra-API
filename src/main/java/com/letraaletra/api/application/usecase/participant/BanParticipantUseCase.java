package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.actor.BanParticipantActorCommand;
import com.letraaletra.api.application.command.participant.BanParticipantCommand;
import com.letraaletra.api.application.context.ModerationContext;
import com.letraaletra.api.application.context.ModerationContextFactory;
import com.letraaletra.api.application.output.participant.BanParticipantOutput;
import com.letraaletra.api.application.port.Actor;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.domain.repository.UserRepository;
import com.letraaletra.api.domain.user.User;
import com.letraaletra.api.infrastructure.manager.GameActorManager;

import java.util.concurrent.CompletableFuture;

public class BanParticipantUseCase {
    private final ModerationContextFactory moderationContextFactory;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GameActorManager gameActorManager;

    public BanParticipantUseCase(ModerationContextFactory moderationContextFactory, UserRepository userRepository, GameRepository gameRepository, GameActorManager gameActorManager) {
        this.moderationContextFactory = moderationContextFactory;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.gameActorManager = gameActorManager;
    }

    public BanParticipantOutput execute(BanParticipantCommand command) {
        ModerationContext context = moderationContextFactory.resolve(command.token(), command.target(), command.user());

        Actor actor = gameActorManager.getOrCreate(context.game().getId());
        CompletableFuture<Game> future = actor.enqueueCommand(new BanParticipantActorCommand(command.target(), command.user()));
        Game game = future.join();

        User user = userRepository.find(command.target());
        user.leaveGame();

        userRepository.save(user);

        if (game.getParticipants().isEmpty()) {
            gameRepository.removeByCode(game.getCode());
        } else {
            gameRepository.save(game);
        }

        return buildReturn(game, command.token());
    }

    private BanParticipantOutput buildReturn(Game game, String token) {
        return new BanParticipantOutput(
                token,
                game
        );
    }
}
