package com.letraaletra.api.features.admin.infrastructure.service;

import com.letraaletra.api.features.admin.application.output.GetApplicationStatusOutput;
import com.letraaletra.api.features.admin.application.port.ApplicationStatusService;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.application.port.SessionRepository;
import com.letraaletra.api.features.user.domain.repository.user.UserRepository;
import com.letraaletra.api.shared.application.port.ActorManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetApplicationStatusService implements ApplicationStatusService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final ActorManager<Game> actorManager;

    @Override
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
