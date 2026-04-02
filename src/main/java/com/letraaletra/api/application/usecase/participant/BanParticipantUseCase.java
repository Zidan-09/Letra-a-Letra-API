package com.letraaletra.api.application.usecase.participant;

import com.letraaletra.api.application.command.participant.BanParticipantCommand;
import com.letraaletra.api.application.service.ModerationContext;
import com.letraaletra.api.application.service.ResolveModerationContext;
import com.letraaletra.api.application.output.participant.BanParticipantOutput;
import com.letraaletra.api.domain.game.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BanParticipantUseCase {
    @Autowired
    private ResolveModerationContext resolveModerationContext;

    @Autowired
    private GameRepository gameRepository;

    public BanParticipantOutput execute(BanParticipantCommand command) {
        ModerationContext context = resolveModerationContext.resolve(command.token(), command.target(), command.user());
        Game game = context.game();

        game.addToBlackList(command.target());

        gameRepository.save(game);

        return buildReturn(game, command.token());
    }

    private BanParticipantOutput buildReturn(Game game, String token) {
        return new BanParticipantOutput(
                token,
                game
        );
    }
}
