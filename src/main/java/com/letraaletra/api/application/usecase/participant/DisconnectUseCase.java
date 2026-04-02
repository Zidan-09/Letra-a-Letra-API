package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.DisconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.DisconnectParticipantOutput;
import com.letraaletra.api.application.context.PlayerGameContextFactory;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;

import java.util.Optional;

public class DisconnectUseCase {
    private final GameRepository gameRepository;
    private final PlayerGameContextFactory playerGameContextFactory;
    private final DisconnectScheduler disconnectScheduler;

    public DisconnectUseCase(GameRepository gameRepository, PlayerGameContextFactory playerGameContextFactory, DisconnectScheduler disconnectScheduler) {
        this.gameRepository = gameRepository;
        this.playerGameContextFactory = playerGameContextFactory;
        this.disconnectScheduler = disconnectScheduler;
    }

    public Optional<DisconnectParticipantOutput> execute(DisconnectParticipantCommand command) {
        return playerGameContextFactory.resolve(command.user())
                .map(ctx -> {
                    disconnectScheduler.start(command.user(), ctx.game().getId());

                    ctx.participant().disconnect();

                    gameRepository.save(ctx.game());

                    return buildReturn(ctx.game(), command.user());
                })
                .orElse(Optional.empty());
    }

    private Optional<DisconnectParticipantOutput> buildReturn(Game game, String user) {
        return Optional.of(
                new DisconnectParticipantOutput(user, game)
        );
    }
}
