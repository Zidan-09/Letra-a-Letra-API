package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.UpdateItemDefinitionInput;
import com.letraaletra.api.features.inventory.application.output.UpdateItemDefinitionOutput;
import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.exception.ItemAlreadyExistsException;
import com.letraaletra.api.features.inventory.domain.exception.ItemNotFoundException;
import com.letraaletra.api.features.inventory.domain.repository.ItemDefinitionRepository;
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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateItemDefinitionUseCase Unit Tests")
class UpdateItemDefinitionUseCaseTest {

    @Mock
    private ItemDefinitionRepository itemDefinitionRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private UpdateItemDefinitionUseCase useCase;

    private AuthenticatedUser principal;
    private ItemDefinition definition;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
        definition = ItemDefinition.create(
                "Blue Avatar",
                ItemKind.COSMETIC,
                ItemCategory.AVATAR,
                Set.of(ItemContext.PROFILE),
                false,
                null,
                false,
                null,
                "/assets/avatar/blue.png"
        );
    }

    @Test
    @DisplayName("update deve checar admin, aplicar parcial, versionar e salvar")
    void updateShouldCheckAdminApplyPartialVersionAndSave() {
        when(itemDefinitionRepository.findById(definition.getId())).thenReturn(Optional.of(definition));
        when(itemDefinitionRepository.findByName("Navy Avatar")).thenReturn(Optional.empty());

        UpdateItemDefinitionOutput output = useCase.execute(new UpdateItemDefinitionInput(
                principal, definition.getId(), "Navy Avatar", null, false));

        verify(adminChecker).check(principal, PermissionKey.COSMETIC, PermissionAction.EDIT);
        assertEquals("Navy Avatar", output.definition().getName());
        assertEquals("/assets/avatar/blue.png", output.definition().getAssetPath());
        assertFalse(output.definition().isAvailable());
        assertEquals(2, output.definition().getVersion());
        verify(itemDefinitionRepository).save(definition);
    }

    @Test
    @DisplayName("definição inexistente deve falhar")
    void missingDefinitionShouldFail() {
        UUID missing = UUID.randomUUID();
        when(itemDefinitionRepository.findById(missing)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> useCase.execute(
                new UpdateItemDefinitionInput(principal, missing, "X", null, null)));
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("nome em uso por outra definição deve falhar")
    void nameInUseByAnotherDefinitionShouldFail() {
        ItemDefinition other = ItemDefinition.create(
                "Navy Avatar",
                ItemKind.COSMETIC,
                ItemCategory.AVATAR,
                Set.of(ItemContext.PROFILE),
                false,
                null,
                false,
                null,
                "/assets/avatar/navy.png"
        );
        when(itemDefinitionRepository.findById(definition.getId())).thenReturn(Optional.of(definition));
        when(itemDefinitionRepository.findByName("Navy Avatar")).thenReturn(Optional.of(other));

        assertThrows(ItemAlreadyExistsException.class, () -> useCase.execute(
                new UpdateItemDefinitionInput(principal, definition.getId(), "Navy Avatar", null, null)));
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }
}
