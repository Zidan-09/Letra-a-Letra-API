package com.letraaletra.api.application.game.usecase;

import com.letraaletra.api.application.game.service.MapParticipantsService;
import com.letraaletra.api.application.game.service.ModerationContext;
import com.letraaletra.api.application.game.service.ResolveModerationContext;
import com.letraaletra.api.domain.Game;
import com.letraaletra.api.domain.repository.GameRepository;
import com.letraaletra.api.infra.websocket.BroadcastService;
import com.letraaletra.api.presentation.dto.mappers.GameDTOMapper;
import com.letraaletra.api.presentation.dto.response.participant.ParticipantDTO;
import com.letraaletra.api.presentation.dto.response.websocket.GameUpdatedWsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BanParticipantUseCase {
    @Autowired
    private ResolveModerationContext resolveModerationContext;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private MapParticipantsService mapParticipantsService;

    @Autowired
    private GameDTOMapper gameDTOMapper;

    @Autowired
    private BroadcastService broadcast;

    public void execute(String tokenGameId, String participantId, String userId) {
        ModerationContext context = resolveModerationContext.resolve(tokenGameId, participantId, userId);
        Game game = context.game();

        game.remove(participantId);
        game.addToBlackList(participantId);

        gameRepository.save(game);

        GameUpdatedWsResponse response = buildResponse(game, tokenGameId);

        broadcast.send(game.getId(), response);
    }

    private GameUpdatedWsResponse buildResponse(Game game, String tokenGameId) {
        List<ParticipantDTO> participantDTOS = mapParticipantsService.execute(game);

        return new GameUpdatedWsResponse(
                gameDTOMapper.toDTO(game, tokenGameId, participantDTOS)
        );
    }
}
