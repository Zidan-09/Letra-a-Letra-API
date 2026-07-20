package com.letraaletra.api.features.admin.application.service;

import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.application.port.ActorManager;

public class GetApplicationStatusService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final ActorManager<Game> actorManager;

    public GetApplicationStatusService(
            UserRepository userRepository,
            SessionRepository sessionRepository,
            ActorManager<Game> actorManager
    ) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.actorManager = actorManager;
    }

    public GetApplicationStatusOutput handle() {

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
