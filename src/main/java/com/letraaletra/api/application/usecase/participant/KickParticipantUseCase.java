package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.KickParticipantCommand;
import com.letraaletra.api.application.service.ModerationContext;
import com.letraaletra.api.application.service.ResolveModerationContext;
import com.letraaletra.api.application.output.participant.KickParticipantOutput;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KickParticipantUseCase {
    @Autowired
    private ResolveModerationContext resolveModerationContext;

    @Autowired
    private GameRepository gameRepository;

    public KickParticipantOutput execute(KickParticipantCommand command) {
        ModerationContext context = resolveModerationContext.resolve(
                command.token(),
                command.target(),
                command.user()
        );

        Game game = context.game();

        game.remove(command.target());

        gameRepository.save(game);

        return buildReturn(game, command.token());
    }

    private KickParticipantOutput buildReturn(Game game, String token) {
        return new KickParticipantOutput(
                token,
                game
        );
    }
}
