package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.BanParticipantActorCommand;
import com.letraaletra.api.features.participant.application.input.BanParticipantInput;
import com.letraaletra.api.features.participant.application.output.ModerationContext;
import com.letraaletra.api.features.participant.application.service.ModerationContextService;
import com.letraaletra.api.features.participant.application.output.BanParticipantOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exceptions.UserNotFoundException;

import java.util.concurrent.CompletableFuture;

public class BanParticipantUseCase implements UseCase<BanParticipantInput, BanParticipantOutput> {
    private final ModerationContextService moderationContextService;
    private final UserRepository userRepository;
    private final ActorManager<Game> gameActorManager;

    public BanParticipantUseCase(ModerationContextService moderationContextService, UserRepository userRepository, ActorManager<Game> gameActorManager) {
        this.moderationContextService = moderationContextService;
        this.userRepository = userRepository;
        this.gameActorManager = gameActorManager;
    }

    public BanParticipantOutput execute(BanParticipantInput input) {
        ModerationContext context = moderationContextService
                .resolve(input.gameId(), input.target(), input.user());

        Actor actor = gameActorManager.get(context.game().getId());
        CompletableFuture<Game> future = actor.enqueueCommand(new BanParticipantActorCommand(input.target(), input.user()));
        Game game = future.join();

        User user = userRepository.find(input.target())
                .orElseThrow(UserNotFoundException::new);

        user.leaveGame();

        userRepository.save(user);

        return buildReturn(game);
    }

    private BanParticipantOutput buildReturn(Game game) {
        return new BanParticipantOutput(
                game
        );
    }
}
