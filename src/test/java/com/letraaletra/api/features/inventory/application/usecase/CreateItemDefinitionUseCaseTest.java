package com.letraaletra.api.features.inventory.application.usecase;

import com.letraaletra.api.features.inventory.application.input.CreateItemDefinitionInput;
import com.letraaletra.api.features.inventory.application.output.CreateItemDefinitionOutput;
import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.ItemKind;
import com.letraaletra.api.features.inventory.domain.exception.ItemAlreadyExistsException;
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
import static org.mockito.Mockito.mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateItemDefinitionUseCase Unit Tests")
class CreateItemDefinitionUseCaseTest {

    @Mock
    private ItemDefinitionRepository itemDefinitionRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private CreateItemDefinitionUseCase useCase;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
    }

    private CreateItemDefinitionInput input(String name) {
        return new CreateItemDefinitionInput(
                principal, name, ItemKind.COSMETIC, ItemCategory.AVATAR,
                Set.of(ItemContext.PROFILE), false, null, false, null, "/assets/avatar/blue.png"
        );
    }

    @Test
    @DisplayName("create deve checar admin, salvar e retornar a definição")
    void createShouldCheckAdminSaveAndReturnDefinition() {
        when(itemDefinitionRepository.findByName("Blue Avatar")).thenReturn(Optional.empty());

        CreateItemDefinitionOutput output = useCase.execute(input("Blue Avatar"));

        verify(adminChecker).check(principal, PermissionKey.COSMETIC, PermissionAction.CREATE);
        assertEquals("Blue Avatar", output.definition().getName());
        assertEquals(Set.of(ItemContext.PROFILE), output.definition().getApplicability());
        verify(itemDefinitionRepository).save(output.definition());
    }

    @Test
    @DisplayName("applicability ausente deve defaultar para PROFILE")
    void missingApplicabilityShouldDefaultToProfile() {
        when(itemDefinitionRepository.findByName("Emote")).thenReturn(Optional.empty());
        CreateItemDefinitionInput withoutApplicability = new CreateItemDefinitionInput(
                principal, "Emote", ItemKind.COSMETIC, ItemCategory.EMOTE,
                null, false, null, false, null, "/assets/emote/wave.png"
        );

        CreateItemDefinitionOutput output = useCase.execute(withoutApplicability);

        assertEquals(Set.of(ItemContext.PROFILE), output.definition().getApplicability());
    }

    @Test
    @DisplayName("nome duplicado deve falhar sem salvar")
    void duplicateNameShouldFailWithoutSaving() {
        when(itemDefinitionRepository.findByName("Blue Avatar"))
                .thenReturn(Optional.of(mock(ItemDefinition.class)));

        assertThrows(ItemAlreadyExistsException.class, () -> useCase.execute(input("Blue Avatar")));
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }
}
