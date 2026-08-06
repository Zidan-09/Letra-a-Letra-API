package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.KickParticipantActorCommand;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.participant.application.input.KickParticipantInput;
import com.letraaletra.api.features.participant.application.output.KickParticipantOutput;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;

import java.util.concurrent.CompletableFuture;

public class KickParticipantUseCase implements UseCase<KickParticipantInput, KickParticipantOutput> {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ActorManager<Game> gameActorManager;

    public KickParticipantUseCase(
            GameRepository gameRepository,
            UserRepository userRepository,
            ActorManager<Game> gameActorManager
    ) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.gameActorManager = gameActorManager;
    }

    @Override
    public KickParticipantOutput execute(KickParticipantInput input) {
        User target = userRepository.find(input.target())
                .orElseThrow(UserNotFoundException::new);

        Actor actor = gameActorManager.get(input.gameId());

        CompletableFuture<Game> future = actor.enqueueCommand(new KickParticipantActorCommand(target, input.user()));
        Game game = future.join();

        userRepository.save(target);
        gameRepository.save(game);

        return new KickParticipantOutput(game);
    }
}
