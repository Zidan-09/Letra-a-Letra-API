package com.letraaletra.api.features.admin.application.usecase;

import com.letraaletra.api.features.admin.application.input.GetApplicationStatusInput;
import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.ActorManager;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.application.usecase.UseCase;

public class GetApplicationStatusUseCase implements UseCase<GetApplicationStatusInput, GetApplicationStatusOutput> {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final ActorManager<Game> actorManager;
    private final AdminChecker adminChecker;

    public GetApplicationStatusUseCase(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            ActorManager<Game> actorManager,
            AdminChecker adminChecker
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.actorManager = actorManager;
        this.adminChecker = adminChecker;
    }

    @Override
    public GetApplicationStatusOutput execute(GetApplicationStatusInput input) {
        adminChecker.check(input.auth());

        long players = userRepository.countUsers();

        long online = sessionRepository.playersOnline();

        long games = actorManager.count();

        return new GetApplicationStatusOutput(
                players,
                online,
                games
        );
    }
}
