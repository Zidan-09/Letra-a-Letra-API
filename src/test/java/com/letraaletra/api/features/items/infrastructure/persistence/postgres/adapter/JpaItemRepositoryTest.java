package com.letraaletra.api.features.items.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.items.domain.*;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.entity.ItemJpaEntity;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.jpa.SpringDataItemRepository;
import com.letraaletra.api.features.items.infrastructure.persistence.postgres.mapper.ItemJpaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaItemRepositoryTest {

    @Mock
    private SpringDataItemRepository springData;

    private JpaItemRepository adapter;

    @BeforeEach
    void setUp() {
        adapter = new JpaItemRepository(springData);
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
    @DisplayName("save deve mapear e delegar ao Spring Data")
    void saveShouldMapAndDelegate() {
        Item item = avatar();

        adapter.save(item);

        ArgumentCaptor<ItemJpaEntity> captor = ArgumentCaptor.forClass(ItemJpaEntity.class);
        verify(springData).save(captor.capture());
        assertEquals(item.getId(), captor.getValue().getId());
        assertEquals("Blue Avatar", captor.getValue().getName());
        assertEquals(ItemKind.EQUIPPABLE, captor.getValue().getKind());
    }

    @Test
    @DisplayName("findById deve retornar o domínio quando existir")
    void findByIdShouldReturnDomainWhenPresent() {
        Item item = avatar();
        UUID id = item.getId();
        when(springData.findById(id))
                .thenReturn(Optional.of(ItemJpaMapper.toEntity(item)));

        Optional<Item> found = adapter.findById(id);

        assertTrue(found.isPresent());
        assertEquals(id, found.get().getId());
        assertTrue(found.get() instanceof EquippableItem);
        assertEquals(EquippableCategory.AVATAR, ((EquippableItem) found.get()).getCategory());
    }

    @Test
    @DisplayName("findById deve retornar vazio quando não existir")
    void findByIdShouldReturnEmptyWhenMissing() {
        UUID id = UUID.randomUUID();
        when(springData.findById(id)).thenReturn(Optional.empty());

        assertTrue(adapter.findById(id).isEmpty());
    }

    @Test
    @DisplayName("findByName deve delegar ao Spring Data")
    void findByNameShouldDelegate() {
        Item item = avatar();
        when(springData.findByName("Blue Avatar"))
                .thenReturn(Optional.of(ItemJpaMapper.toEntity(item)));

        Optional<Item> found = adapter.findByName("Blue Avatar");

        assertTrue(found.isPresent());
        assertEquals(item.getId(), found.get().getId());
        verify(springData).findByName("Blue Avatar");
    }

    @Test
    @DisplayName("findByName deve retornar vazio quando não existir")
    void findByNameShouldReturnEmptyWhenMissing() {
        when(springData.findByName("missing")).thenReturn(Optional.empty());

        assertTrue(adapter.findByName("missing").isEmpty());
    }

    @Test
    @DisplayName("findAll deve repassar filtro e página e mapear o conteúdo")
    void findAllShouldDelegateFilterAndPage() {
        Item item = avatar();
        ItemJpaEntity entity = ItemJpaMapper.toEntity(item);
        when(springData.search(any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));

        var page = adapter.findAll(
                new ItemFilter(ItemKind.EQUIPPABLE, null, null),
                new ItemsPage(0, 20, Sort.unsorted()));

        assertEquals(1, page.getContent().size());
        assertEquals(item.getId(), page.getContent().get(0).getId());
        verify(springData).search(
                org.mockito.ArgumentMatchers.eq(ItemKind.EQUIPPABLE),
                org.mockito.ArgumentMatchers.isNull(),
                org.mockito.ArgumentMatchers.isNull(),
                any(Pageable.class));
    }

    @Test
    @DisplayName("delete deve delegar ao Spring Data pelo id")
    void deleteShouldDelegateById() {
        Item item = avatar();

        adapter.delete(item);

        verify(springData).deleteById(item.getId());
    }
}
