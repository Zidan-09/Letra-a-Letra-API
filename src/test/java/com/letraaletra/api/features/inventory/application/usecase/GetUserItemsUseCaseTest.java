package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.GetUserItemsInput;
import com.letraaletra.api.features.inventory.application.output.GetUserItemsOutput;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionLookup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserItemsUseCase Unit Tests")
class GetUserItemsUseCaseTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemDefinitionLookup itemLookup;

    @InjectMocks
    private GetUserItemsUseCase useCase;

    private UUID userId;
    private ItemDefinition avatar;
    private ItemDefinition boost;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        avatar = ItemDefinition.create(
                "Blue Avatar",
                ItemKind.COSMETIC,
                ItemCategory.AVATAR,
                ItemContext.PROFILE,
                false,
                null,
                false,
                null,
                "/assets/avatar/blue.png"
        );
        boost = ItemDefinition.create(
                "XP Boost",
                ItemKind.CONSUMABLE,
                ItemCategory.XP_BOOST,
                ItemContext.PROFILE,
                true,
                1000,
                true,
                new com.letraaletra.api.features.items.domain.PercentageTimedEffect(
                        com.letraaletra.api.features.items.domain.EffectType.XP_BOOST_PCT, 50, 60),
                null
        );

        lenient().when(itemLookup.getById(avatar.getId())).thenReturn(avatar);
        lenient().when(itemLookup.getById(boost.getId())).thenReturn(boost);
        lenient().when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of(
                UserItem.restore(userId, avatar.getId(), 1, true, LocalDateTime.now(), null),
                UserItem.restore(userId, boost.getId(), 3, false, LocalDateTime.now(), null)
        ));
    }

    @Test
    @DisplayName("sem filtros deve retornar todos os itens com definições")
    void withoutFiltersShouldReturnAllItems() {
        GetUserItemsOutput output = useCase.execute(
                new GetUserItemsInput(userId, null, null, null, null));

        assertEquals(2, output.items().size());
        assertEquals(avatar.getId(), output.items().get(0).definition().getId());
        assertEquals(boost.getId(), output.items().get(1).definition().getId());
    }

    @Test
    @DisplayName("filtros devem restringir por kind, category, context e equipped")
    void filtersShouldRestrictResults() {
        assertEquals(1, useCase.execute(
                new GetUserItemsInput(userId, ItemKind.CONSUMABLE, null, null, null)).items().size());

        assertEquals(1, useCase.execute(
                new GetUserItemsInput(userId, null, ItemCategory.AVATAR, null, null)).items().size());

        assertEquals(2, useCase.execute(
                new GetUserItemsInput(userId, null, null, ItemContext.PROFILE, null)).items().size());

        assertEquals(0, useCase.execute(
                new GetUserItemsInput(userId, null, null, ItemContext.MATCH, null)).items().size());

        assertEquals(1, useCase.execute(
                new GetUserItemsInput(userId, null, null, null, true)).items().size());
    }

    @Test
    @DisplayName("definição ausente deve falhar explícito")
    void missingDefinitionShouldFailExplicitly() {
        UUID unknownId = UUID.randomUUID();
        when(itemLookup.getById(unknownId)).thenThrow(new ItemNotFoundException());
        when(inventoryRepository.findItemsByOwner(userId)).thenReturn(List.of(
                UserItem.restore(userId, unknownId, 1, false, LocalDateTime.now(), null)
        ));

        assertThrows(ItemNotFoundException.class, () -> useCase.execute(
                new GetUserItemsInput(userId, null, null, null, null)));
    }
}
