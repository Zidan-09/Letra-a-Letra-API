package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.GetItemDefinitionInput;
import com.letraaletra.api.features.items.application.output.GetItemDefinitionOutput;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemKind;
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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetItemDefinitionUseCase Unit Tests")
class GetItemDefinitionUseCaseTest {

    @Mock
    private ItemDefinitionRepository itemDefinitionRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private GetItemDefinitionUseCase useCase;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
    }

    private ItemDefinition avatar() {
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
    @DisplayName("buscar existente deve checar permissao e retornar a definicao")
    void existingShouldCheckPermissionAndReturnDefinition() {
        ItemDefinition definition = avatar();
        when(itemDefinitionRepository.findById(definition.getId())).thenReturn(Optional.of(definition));

        GetItemDefinitionOutput output = useCase.execute(new GetItemDefinitionInput(principal, definition.getId()));

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.VIEW);
        assertSame(definition, output.definition());
        assertEquals("Blue Avatar", output.definition().getName());
    }

    @Test
    @DisplayName("definicao ausente deve falhar sem salvar")
    void missingShouldFailWithoutSaving() {
        UUID missing = UUID.randomUUID();
        when(itemDefinitionRepository.findById(missing)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> useCase.execute(new GetItemDefinitionInput(principal, missing)));
        verify(itemDefinitionRepository, never()).save(ArgumentMatchers.any());
    }
}
