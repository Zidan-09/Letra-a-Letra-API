package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.ReconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.ReconnectParticipantOutput;
import com.letraaletra.api.application.service.PlayerGameContextResolver;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReconnectUseCase {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private DisconnectScheduler disconnectScheduler;

    @Autowired
    private PlayerGameContextResolver playerGameContextResolver;

    public Optional<ReconnectParticipantOutput> execute(ReconnectParticipantCommand command) {
        return playerGameContextResolver.resolve(command.user())
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
