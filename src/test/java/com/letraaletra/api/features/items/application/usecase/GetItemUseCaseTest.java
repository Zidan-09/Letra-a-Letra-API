package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.GetItemInput;
import com.letraaletra.api.features.items.application.output.GetItemOutput;
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
@DisplayName("GetItemUseCase Unit Tests")
class GetItemUseCaseTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private GetItemUseCase useCase;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
    }

    private Item avatar() {
        return EquippableItem.create(
                "Blue Avatar",
                EquippableContext.PROFILE,
                EquippableCategory.AVATAR,
                "/assets/avatar/blue.png"
        );
    }

    @Test
    @DisplayName("buscar existente deve checar permissao e retornar a definicao")
    void existingShouldCheckPermissionAndReturnDefinition() {
        Item item = avatar();
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        GetItemOutput output = useCase.execute(new GetItemInput(principal, item.getId()));

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.VIEW);
        assertSame(item, output.item());
        assertEquals("Blue Avatar", output.item().getName());
    }

    @Test
    @DisplayName("definicao ausente deve falhar sem salvar")
    void missingShouldFailWithoutSaving() {
        UUID missing = UUID.randomUUID();
        when(itemRepository.findById(missing)).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> useCase.execute(new GetItemInput(principal, missing)));
        verify(itemRepository, never()).save(ArgumentMatchers.any());
    }
}
