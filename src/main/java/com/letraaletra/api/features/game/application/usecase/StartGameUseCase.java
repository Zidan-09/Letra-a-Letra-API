package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.audit.domain.AuditActor;
import com.letraaletra.api.features.audit.domain.AuditActorType;
import com.letraaletra.api.features.audit.domain.AuditCategory;
import com.letraaletra.api.features.audit.domain.AuditEvent;
import com.letraaletra.api.features.audit.domain.AuditEventType;
import com.letraaletra.api.features.audit.domain.AuditResourceType;
import com.letraaletra.api.features.audit.domain.AuditSourceType;
import com.letraaletra.api.features.game.application.port.SelectThemeService;
import com.letraaletra.api.features.game.domain.actor.command.StartCustomGameActorCommand;
import com.letraaletra.api.features.game.application.input.StartGameInput;
import com.letraaletra.api.features.game.domain.board.generator.BoardGenerator;
import com.letraaletra.api.features.game.domain.repository.GameRepository;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.features.audit.application.port.BusinessAuditRecorder;
import com.letraaletra.api.features.game.domain.room.port.RoomTimeoutManager;
import com.letraaletra.api.features.game.application.output.StartGameOutput;
import com.letraaletra.api.features.game.domain.turn.port.TurnTimeoutManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.board.Board;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class StartGameUseCase implements UseCase<StartGameInput, StartGameOutput> {
    private static final String SOURCE_DETAIL = "START_GAME";

    private final GameRepository gameRepository;
    private final RoomTimeoutManager roomTimeoutManager;
    private final SelectThemeService themeService;
    private final TurnTimeoutManager turnTimeoutManager;
    private final ActorManager<Game> gameActorManager;
    private final BusinessAuditRecorder auditRecorder;

    public StartGameUseCase(
            GameRepository gameRepository,
            RoomTimeoutManager roomTimeoutManager,
            SelectThemeService themeService,
            TurnTimeoutManager turnTimeoutManager,
            ActorManager<Game> gameActorManager,
            BusinessAuditRecorder auditRecorder
    ) {
        this.gameRepository = gameRepository;
        this.roomTimeoutManager = roomTimeoutManager;
        this.themeService = themeService;
        this.turnTimeoutManager = turnTimeoutManager;
        this.gameActorManager = gameActorManager;
        this.auditRecorder = auditRecorder;
    }

    @Override
    public StartGameOutput execute(StartGameInput input) {
        List<String> words = themeService.select(input.settings().getThemeId());

        Board board = BoardGenerator.generate(words, input.settings().getGameMode());

        Actor actor = gameActorManager.get(input.gameId());

        CompletableFuture<Game> future = actor.enqueueCommand(new StartCustomGameActorCommand(input.session(), board, roomTimeoutManager, turnTimeoutManager));

        Game game = future.join();

        recordMatchStarted(game);

        return new StartGameOutput(game);
    }

    private void recordMatchStarted(Game game) {
        if (game.getGameState() == null || game.getGameState().getMatchId() == null) {
            return;
        }

        auditRecorder.record(AuditEvent.builder()
                .category(AuditCategory.GAME)
                .eventType(AuditEventType.MATCH_STARTED)
                .actor(new AuditActor(AuditActorType.USER, game.getHostId(), null))
                .resourceType(AuditResourceType.MATCH)
                .resourceId(game.getGameState().getMatchId().toString())
                .correlationId(game.getId().toString())
                .sourceType(AuditSourceType.WEBSOCKET)
                .sourceDetail(SOURCE_DETAIL)
                .metadata(Map.of("gameId", game.getId().toString()))
                .build());
    }
}
