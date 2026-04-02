package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.DisconnectParticipantCommand;
import com.letraaletra.api.application.output.participant.DisconnectParticipantOutput;
import com.letraaletra.api.application.service.PlayerGameContextResolver;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DisconnectUseCase {

    @Autowired
    private DisconnectScheduler disconnectScheduler;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerGameContextResolver playerGameContextResolver;

    public Optional<DisconnectParticipantOutput> execute(DisconnectParticipantCommand command) {
        return playerGameContextResolver.resolve(command.user())
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
