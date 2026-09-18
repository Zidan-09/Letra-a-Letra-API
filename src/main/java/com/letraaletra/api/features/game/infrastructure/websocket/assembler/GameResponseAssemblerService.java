package com.letraaletra.api.features.game.infrastructure.websocket.assembler;

import com.letraaletra.api.features.game.application.output.HandledGameOver;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.infrastructure.presentation.mapper.game.GameOverMapper;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.participant.domain.EquippedCosmetic;
import com.letraaletra.api.features.participant.domain.Participant;
import com.letraaletra.api.features.ranking.infrastructure.presentation.dto.response.RankedMatchResult;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.RankingMatchResultMapper;
import com.letraaletra.api.features.ranking.infrastructure.presentation.mapper.RankingOverResultMapper;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.shared.infrastructure.websocket.WsConnectionRegistry;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameOver;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.shared.infrastructure.presentation.dto.response.WsResponse;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.UUID;

public class GameResponseAssemblerService implements GameResponseAssembler {
    private final UserRepository userRepository;
    private final WsConnectionRegistry connectionRegistry;
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;

    public GameResponseAssemblerService(
            UserRepository userRepository,
            WsConnectionRegistry connectionRegistry
    ) {
        this(userRepository, connectionRegistry, null, null);
    }

    public GameResponseAssemblerService(
            UserRepository userRepository,
            WsConnectionRegistry connectionRegistry,
            InventoryRepository inventoryRepository,
            ItemRepository itemRepository
    ) {
        this.userRepository = userRepository;
        this.connectionRegistry = connectionRegistry;
        this.inventoryRepository = inventoryRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public WsResponse assembleGameOver(Game game, GameOver gameOver, HandledGameOver handledGameOver) {
        User winner = userRepository.find(gameOver.winner().getUserId())
                .orElseThrow(UserNotFoundException::new);

        User loser = userRepository.find(gameOver.loser().getUserId())
                .orElseThrow(UserNotFoundException::new);

        WebSocketSession winnerSession = connectionRegistry.findByUserId(winner.getUserId());
        WebSocketSession loserSession = connectionRegistry.findByUserId(loser.getUserId());

        Participant winnerParticipant = Participant.create(
                winner,
                winnerSession != null ? winnerSession.getId() : "",
                equippedItems(winner.getUserId())
        );

        Participant loserParticipant = Participant.create(
                loser,
                loserSession != null ? loserSession.getId() : "",
                equippedItems(loser.getUserId())
        );

        if (game.getGameType().equals(GameType.RANKING)) {
            RankedMatchResult winnerResult = RankingMatchResultMapper.toResponse(
                    gameOver.winner(),
                    winnerParticipant,
                    handledGameOver.winnerPoints().orElseThrow()
            );

            RankedMatchResult loserResult = RankingMatchResultMapper.toResponse(
                    gameOver.loser(),
                    loserParticipant,
                    handledGameOver.loserPoints().orElseThrow()
            );

            return RankingOverResultMapper.toResponse(winnerResult, loserResult);
        }

        return GameOverMapper.toResponse(
                gameOver,
                winnerParticipant,
                loserParticipant
        );
    }

    private List<EquippedCosmetic> equippedItems(UUID userId) {
        if (inventoryRepository == null || itemRepository == null) {
            return List.of();
        }
        List<UserItem> items = inventoryRepository.findItemsByOwner(userId);
        return EquippedCosmetic.fromProfileItems(
                items,
                itemId -> itemRepository.findById(itemId)
                        .orElseThrow(com.letraaletra.api.features.items.domain.exception.ItemNotFoundException::new),
                EquippableContext.PROFILE
        );
    }
}
