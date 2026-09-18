package com.letraaletra.api.features.items.application.usecase;

import com.letraaletra.api.features.items.application.input.ListItemDefinitionsInput;
import com.letraaletra.api.features.items.application.output.ListItemDefinitionsOutput;
import com.letraaletra.api.features.items.domain.ItemCategory;
import com.letraaletra.api.features.items.domain.ItemContext;
import com.letraaletra.api.features.items.domain.ItemDefinition;
import com.letraaletra.api.features.items.domain.ItemDefinitionFilter;
import com.letraaletra.api.features.items.domain.ItemsPage;
import com.letraaletra.api.features.items.domain.ItemKind;
import com.letraaletra.api.features.items.domain.repository.ItemDefinitionRepository;
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
@DisplayName("ListItemDefinitionsUseCase Unit Tests")
class ListItemDefinitionsUseCaseTest {

    @Mock
    private ItemDefinitionRepository itemDefinitionRepository;

    @Mock
    private AdminChecker adminChecker;

    @InjectMocks
    private ListItemDefinitionsUseCase useCase;

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

    private ListItemDefinitionsInput input() {
        return new ListItemDefinitionsInput(principal, ItemKind.COSMETIC, null, null, 0, 20, Sort.unsorted());
    }

    @Test
    @DisplayName("listar com filtro deve checar permissao, repassar filtro e retornar 1 item")
    void listWithFilterShouldCheckPermissionAndReturnOneItem() {
        ItemDefinition avatar = avatar();
        when(itemDefinitionRepository.findAll(any(ItemDefinitionFilter.class), any(ItemsPage.class)))
                .thenReturn(new PageImpl<>(List.of(avatar)));

        ListItemDefinitionsOutput output = useCase.execute(input());

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.VIEW);
        assertEquals(1, output.definitions().getContent().size());
        assertSame(avatar, output.definitions().getContent().get(0));

        ArgumentCaptor<ItemDefinitionFilter> filterCaptor = ArgumentCaptor.forClass(ItemDefinitionFilter.class);
        ArgumentCaptor<ItemsPage> pageCaptor = ArgumentCaptor.forClass(ItemsPage.class);
        verify(itemDefinitionRepository).findAll(filterCaptor.capture(), pageCaptor.capture());
        assertEquals(ItemKind.COSMETIC, filterCaptor.getValue().kind());
    }

    @Test
    @DisplayName("pagina vazia deve retornar 0 itens")
    void emptyPageShouldReturnZeroItems() {
        when(itemDefinitionRepository.findAll(any(ItemDefinitionFilter.class), any(ItemsPage.class)))
                .thenReturn(new PageImpl<>(List.of()));

        ListItemDefinitionsOutput output = useCase.execute(input());

        verify(adminChecker).check(principal, PermissionKey.ITEMS, PermissionAction.VIEW);
        assertEquals(0, output.definitions().getContent().size());
    }
}
