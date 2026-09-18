package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.ToggleItemAvailabilityInput;
import com.letraaletra.api.features.items.application.output.ToggleItemAvailabilityOutput;
import com.letraaletra.api.features.items.domain.*;
import com.letraaletra.api.features.items.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
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
    private ItemRepository itemRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private ToggleItemAvailabilityUseCase useCase;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
    }

    private Item availableAvatar() {
        return EquippableItem.create(
                "Blue Avatar",
                EquippableContext.PROFILE,
                ItemCategory.AVATAR,
                "/assets/avatar/blue.png"
        );
    }

    @Test
    @DisplayName("desativar item disponivel deve salvar sem incrementar a versao")
    void disableAvailableItemShouldSaveWithoutVersionIncrement() {
        Item item = availableAvatar();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ToggleItemAvailabilityOutput output = useCase.execute(
                new ToggleItemAvailabilityInput(principal, item.getId(), false));

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.EDIT);
        assertFalse(output.item().isAvailable());
        assertEquals(1, output.item().getVersion());
        verify(itemRepository).save(item);
    }

    @Test
    @DisplayName("ativar item indisponivel deve salvar sem incrementar a versao")
    void enableUnavailableItemShouldSaveWithoutVersionIncrement() {
        Item item = availableAvatar();
        item.disable();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        ToggleItemAvailabilityOutput output = useCase.execute(
                new ToggleItemAvailabilityInput(principal, item.getId(), true));

        assertTrue(output.item().isAvailable());
        assertEquals(1, output.item().getVersion());
        verify(itemRepository).save(item);
    }

    @Test
    @DisplayName("item inexistente deve falhar sem salvar")
    void unknownItemShouldFailWithoutSaving() {
        UUID itemId = UUID.randomUUID();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> useCase.execute(
                new ToggleItemAvailabilityInput(principal, itemId, false)));
        verify(itemRepository, never()).save(ArgumentMatchers.any());
    }
}
