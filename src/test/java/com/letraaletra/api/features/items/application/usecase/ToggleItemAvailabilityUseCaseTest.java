package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.ToggleItemAvailabilityInput;
import com.letraaletra.api.features.items.application.output.ToggleItemAvailabilityOutput;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.exception.InvalidItemException;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ToggleItemAvailabilityUseCase Unit Tests")
class ToggleItemAvailabilityUseCaseTest {

    @Mock
    private ItemDefinitionRepository itemDefinitionRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private ToggleItemAvailabilityUseCase useCase;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
    }

    private ItemDefinition availableAvatar() {
        return ItemDefinition.create(
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
    }

    @Test
    @DisplayName("desativar item disponivel deve salvar sem incrementar a versao")
    void disableAvailableItemShouldSaveWithoutVersionIncrement() {
        ItemDefinition definition = availableAvatar();
        when(itemDefinitionRepository.findById(definition.getId())).thenReturn(Optional.of(definition));

        ToggleItemAvailabilityOutput output = useCase.execute(
                new ToggleItemAvailabilityInput(principal, definition.getId(), false));

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.EDIT);
        assertFalse(output.definition().isAvailable());
        assertEquals(1, output.definition().getVersion());
        verify(itemDefinitionRepository).save(definition);
    }

    @Test
    @DisplayName("ativar item indisponivel deve salvar sem incrementar a versao")
    void enableUnavailableItemShouldSaveWithoutVersionIncrement() {
        ItemDefinition definition = availableAvatar();
        definition.setAvailable(false);
        when(itemDefinitionRepository.findById(definition.getId())).thenReturn(Optional.of(definition));

        ToggleItemAvailabilityOutput output = useCase.execute(
                new ToggleItemAvailabilityInput(principal, definition.getId(), true));

        assertTrue(output.definition().isAvailable());
        assertEquals(1, output.definition().getVersion());
        verify(itemDefinitionRepository).save(definition);
    }

    @Test
    @DisplayName("item inexistente deve falhar sem salvar")
    void unknownItemShouldFailWithoutSaving() {
        UUID itemId = UUID.randomUUID();
        when(itemDefinitionRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> useCase.execute(
                new ToggleItemAvailabilityInput(principal, itemId, false)));
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("available nulo deve falhar sem salvar")
    void nullAvailableShouldFailWithoutSaving() {
        ItemDefinition definition = availableAvatar();

        assertThrows(InvalidItemException.class, () -> useCase.execute(
                new ToggleItemAvailabilityInput(principal, definition.getId(), null)));
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }
}
