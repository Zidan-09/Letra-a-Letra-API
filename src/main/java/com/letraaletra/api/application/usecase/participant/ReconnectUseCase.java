package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.ReconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.ReconnectParticipantOutput;
import com.letraaletra.api.application.context.PlayerGameContextFactory;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;

import java.util.Optional;

public class ReconnectUseCase {
    private final GameRepository gameRepository;
    private final PlayerGameContextFactory playerGameContextFactory;
    private final DisconnectScheduler disconnectScheduler;

    public ReconnectUseCase(GameRepository gameRepository, PlayerGameContextFactory playerGameContextFactory, DisconnectScheduler disconnectScheduler) {
        this.gameRepository = gameRepository;
        this.playerGameContextFactory = playerGameContextFactory;
        this.disconnectScheduler = disconnectScheduler;
    }

    public Optional<ReconnectParticipantOutput> execute(ReconnectParticipantCommand command) {
        return playerGameContextFactory.resolve(command.user())
                .map(ctx -> {
                    disconnectScheduler.cancel(command.user(), ctx.game().getId());

                    ctx.game().reconnect(command.user(), command.session());

                    gameRepository.save(ctx.game());

                    return buildReturn(ctx.game());
                })
                .orElse(Optional.empty());
    }

    private Optional<ReconnectParticipantOutput> buildReturn(Game game) {
        return Optional.of(new ReconnectParticipantOutput(
                game
        ));
    }
}
