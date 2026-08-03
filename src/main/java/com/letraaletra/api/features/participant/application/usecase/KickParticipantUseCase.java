package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.KickParticipantActorCommand;
import com.letraaletra.api.features.participant.application.input.KickParticipantInput;
import com.letraaletra.api.features.participant.application.output.ModerationContext;
import com.letraaletra.api.features.participant.application.service.ModerationContextService;
import com.letraaletra.api.features.participant.application.output.KickParticipantOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;

import java.util.concurrent.CompletableFuture;

public class KickParticipantUseCase implements UseCase<KickParticipantInput, KickParticipantOutput> {
    private final ModerationContextService moderationContextService;
    private final UserRepository userRepository;
    private final ActorManager<Game> gameActorManager;

    public KickParticipantUseCase(
            ModerationContextService moderationContextService,
            UserRepository userRepository,
            ActorManager<Game> gameActorManager
    ) {
        this.moderationContextService = moderationContextService;
        this.userRepository = userRepository;
        this.gameActorManager = gameActorManager;
    }

    @Override
    public KickParticipantOutput execute(KickParticipantInput input) {
        ModerationContext context = moderationContextService.resolve(
                input.gameId(),
                input.target(),
                input.user()
        );

        Actor actor = gameActorManager.get(context.game().getId());

        CompletableFuture<Game> future = actor.enqueueCommand(new KickParticipantActorCommand(input.target(), input.user()));
        Game game = future.join();

        User user = userRepository.find(input.target())
                .orElseThrow(UserNotFoundException::new);

        user.leaveGame();

        userRepository.save(user);

        return new KickParticipantOutput(game);
    }
}
