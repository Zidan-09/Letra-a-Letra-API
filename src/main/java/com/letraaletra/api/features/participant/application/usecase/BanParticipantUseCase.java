package com.letraaletra.api.features.participant.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.BanParticipantActorCommand;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.participant.application.input.BanParticipantInput;
import com.letraaletra.api.features.participant.application.output.BanParticipantOutput;
import com.letraaletra.api.shared.application.port.Actor;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

public class BanParticipantUseCase implements UseCase<BanParticipantInput, BanParticipantOutput> {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ActorManager<Game> gameActorManager;

    public BanParticipantUseCase(
            UserRepository userRepository,
            GameRepository gameRepository,
            ActorManager<Game> gameActorManager
    ) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.gameActorManager = gameActorManager;
    }

    @Override
    @Transactional
    public BanParticipantOutput execute(BanParticipantInput input) {
        User target = userRepository.find(input.target())
                .orElseThrow(UserNotFoundException::new);

        Actor actor = gameActorManager.get(input.gameId());
        CompletableFuture<Game> future = actor.enqueueCommand(new BanParticipantActorCommand(target, input.user()));
        Game game = future.join();

        userRepository.save(target);
        gameRepository.save(game);

        return new BanParticipantOutput(game);
    }
}
