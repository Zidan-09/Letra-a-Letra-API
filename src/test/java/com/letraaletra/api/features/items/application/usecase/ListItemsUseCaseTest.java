package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.ListItemsInput;
import com.letraaletra.api.features.items.application.output.ListItemsOutput;
import com.letraaletra.api.features.items.domain.*;
import com.letraaletra.api.features.items.domain.repository.ItemRepository;
import com.letraaletra.api.shared.application.port.AdminChecker;
import com.letraaletra.api.shared.domain.AuthenticatedUser;
import com.letraaletra.api.shared.domain.security.PermissionAction;
import com.letraaletra.api.shared.domain.security.PermissionKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ListItemsUseCase Unit Tests")
class ListItemsUseCaseTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private ListItemsUseCase useCase;

    private AuthenticatedUser principal;

    @BeforeEach
    void setUp() {
        principal = new AuthenticatedUser(UUID.randomUUID(), "admin", true, false);
    }

    private Item avatar() {
        return EquippableItem.create(
                "Blue Avatar",
                EquippableContext.PROFILE,
                ItemCategory.AVATAR,
                "/assets/avatar/blue.png"
        );
    }

    private ListItemsInput input() {
        return new ListItemsInput(principal, ItemKind.EQUIPPABLE, null, null, 0, 20, Sort.unsorted());
    }

    @Test
    @DisplayName("listar com filtro deve checar permissao, repassar filtro e retornar 1 item")
    void listWithFilterShouldCheckPermissionAndReturnOneItem() {
        Item avatar = avatar();
        when(itemRepository.findAll(any(ItemFilter.class), any(ItemsPage.class)))
                .thenReturn(new PageImpl<>(List.of(avatar)));

        ListItemsOutput output = useCase.execute(input());

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.VIEW);
        assertEquals(1, output.items().getContent().size());
        assertSame(avatar, output.items().getContent().get(0));

        ArgumentCaptor<ItemFilter> filterCaptor = ArgumentCaptor.forClass(ItemFilter.class);
        ArgumentCaptor<ItemsPage> pageCaptor = ArgumentCaptor.forClass(ItemsPage.class);
        verify(itemRepository).findAll(filterCaptor.capture(), pageCaptor.capture());
        assertEquals(ItemKind.EQUIPPABLE, filterCaptor.getValue().kind());
    }

    @Test
    @DisplayName("pagina vazia deve retornar 0 itens")
    void emptyPageShouldReturnZeroItems() {
        when(itemRepository.findAll(any(ItemFilter.class), any(ItemsPage.class)))
                .thenReturn(new PageImpl<>(List.of()));

        ListItemsOutput output = useCase.execute(input());

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.VIEW);
        assertEquals(0, output.items().getContent().size());
    }
}
