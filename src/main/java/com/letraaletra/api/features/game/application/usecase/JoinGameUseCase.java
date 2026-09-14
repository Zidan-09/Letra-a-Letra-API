package com.letraaletra.api.features.game.application.usecase;

import com.letraaletra.api.features.game.domain.actor.command.JoinGameActorCommand;
import com.letraaletra.api.features.game.application.input.JoinGameInput;
import com.letraaletra.api.features.game.application.output.JoinGameOutput;
import com.letraaletra.api.features.user.domain.exception.UserAlreadyInGameException;
import com.letraaletra.api.features.game.application.port.Actor;
import com.letraaletra.api.features.game.application.port.ActorManager;
import com.letraaletra.api.shared.application.usecase.UseCase;
import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.user.domain.repository.UserRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.exception.UserNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.participant.domain.EquippedCosmetic;
import com.letraaletra.api.features.inventory.domain.ItemContext;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.List;

public class JoinGameUseCase implements UseCase<JoinGameInput, JoinGameOutput> {
    private final UserRepository userRepository;
    private final ActorManager<Game> actorManager;
    private final InventoryRepository inventoryRepository;
    private final ItemDefinitionRepository itemDefinitionRepository;

    public JoinGameUseCase(
        UserRepository userRepository, 
        ActorManager<Game> actorManager,
        InventoryRepository inventoryRepository,
        ItemDefinitionRepository itemDefinitionRepository
    ) {
        this.userRepository = userRepository;
        this.actorManager = actorManager;
        this.inventoryRepository = inventoryRepository;
        this.itemDefinitionRepository = itemDefinitionRepository;
    }

    @Override
    public JoinGameOutput execute(JoinGameInput input) {
        UUID gameId = input.gameId();

        User user = userRepository.find(input.user())
                .orElseThrow(UserNotFoundException::new);

        validateUser(user);

        List<UserItem> items = inventoryRepository.findItemsByOwner(user.getUserId());

        List<EquippedCosmetic> equipped = EquippedCosmetic.fromProfileItems(
                items,
                definitionId -> itemDefinitionRepository.findById(definitionId)
                        .orElseThrow(com.letraaletra.api.features.inventory.domain.exception.ItemNotFoundException::new),
                ItemContext.PROFILE
        );

        Actor actor = actorManager.get(gameId);

        CompletableFuture<Game> future =
                actor.enqueueCommand(
                        new JoinGameActorCommand(
                                user,
                                input.session(),
                                equipped
                        )
                );

        Game game = future.join();

        userRepository.save(user);

        return new JoinGameOutput(game);
    }

    private void validateUser(User user) {
        if (!user.isNotInGame()) {
            throw new UserAlreadyInGameException();
        }
    }
}
