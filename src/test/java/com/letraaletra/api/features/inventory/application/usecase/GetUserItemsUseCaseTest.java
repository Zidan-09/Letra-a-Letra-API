package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.GetUserItemsInput;
import com.letraaletra.api.features.inventory.application.output.GetUserItemsOutput;
import com.letraaletra.api.features.items.domain.item.Item;
import com.letraaletra.api.features.items.domain.item.ItemKind;
import com.letraaletra.api.features.items.domain.consumable.ConsumableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableItem;
import com.letraaletra.api.features.items.domain.equippable.EquippableCategory;
import com.letraaletra.api.features.items.domain.equippable.EquippableContext;
import com.letraaletra.api.features.items.domain.effect.ItemEffect;
import com.letraaletra.api.features.items.domain.effect.EffectType;
import com.letraaletra.api.features.items.domain.effect.PercentageTimedEffect;
import com.letraaletra.api.features.items.domain.effect.NicknameChangeEffect;
import com.letraaletra.api.features.items.domain.catalog.ItemFilter;
import com.letraaletra.api.features.items.domain.catalog.ItemsPage;
import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.InventoryRepository;
import com.letraaletra.api.features.items.domain.repository.ItemLookup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
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
    private ItemLookup itemLookup;

    @InjectMocks
    private GetUserItemsUseCase useCase;

    private UUID userId;
    private EquippableItem avatar;
    private ConsumableItem boost;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        avatar = EquippableItem.create(
                "Blue Avatar",
                EquippableContext.PROFILE,
                EquippableCategory.AVATAR,
                "/assets/avatar/blue.png"
        );
        boost = ConsumableItem.create(
                "XP Boost",
                
                new com.letraaletra.api.features.items.domain.PercentageTimedEffect(
                        com.letraaletra.api.features.items.domain.EffectType.XP_BOOST_PCT, 50, 60)
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
        assertEquals(avatar.getId(), output.items().get(0).item().getId());
        assertEquals(boost.getId(), output.items().get(1).item().getId());
    }

    @Test
    @DisplayName("filtros devem restringir por kind, category, context e equipped")
    void filtersShouldRestrictResults() {
        assertEquals(1, useCase.execute(
                new GetUserItemsInput(userId, ItemKind.CONSUMABLE, null, null, null)).items().size());

        assertEquals(1, useCase.execute(
                new GetUserItemsInput(userId, null, EquippableCategory.AVATAR, null, null)).items().size());

        assertEquals(2, useCase.execute(
                new GetUserItemsInput(userId, null, null, null, null)).items().size());

        assertEquals(0, useCase.execute(
                new GetUserItemsInput(userId, null, null, EquippableContext.MATCH, null)).items().size());

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
