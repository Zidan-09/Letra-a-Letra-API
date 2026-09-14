package com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.inventory.domain.ItemCategory;
import com.letraaletra.api.features.inventory.domain.ItemContext;
import com.letraaletra.api.features.inventory.domain.ItemDefinition;
import com.letraaletra.api.features.inventory.domain.ItemKind;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.entity.ItemDefinitionJpaEntity;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.jpa.SpringDataItemDefinitionRepository;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.mapper.ItemDefinitionJpaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaItemDefinitionRepositoryTest {

    @Mock
    private SpringDataItemDefinitionRepository springData;

    private JpaItemDefinitionRepository adapter;

    @BeforeEach
    void setUp() {
        adapter = new JpaItemDefinitionRepository(springData);
    }

    private ItemDefinition avatar() {
        return ItemDefinition.create(
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
    @DisplayName("save deve mapear e delegar ao Spring Data")
    void saveShouldMapAndDelegate() {
        ItemDefinition definition = avatar();

        adapter.save(definition);

        ArgumentCaptor<ItemDefinitionJpaEntity> captor = ArgumentCaptor.forClass(ItemDefinitionJpaEntity.class);
        verify(springData).save(captor.capture());
        assertEquals(definition.getId(), captor.getValue().getId());
        assertEquals("Blue Avatar", captor.getValue().getName());
        assertEquals(ItemKind.COSMETIC, captor.getValue().getKind());
    }

    @Test
    @DisplayName("findById deve retornar o domínio quando existir")
    void findByIdShouldReturnDomainWhenPresent() {
        ItemDefinition definition = avatar();
        UUID id = definition.getId();
        when(springData.findById(id))
                .thenReturn(Optional.of(ItemDefinitionJpaMapper.toEntity(definition)));

        Optional<ItemDefinition> found = adapter.findById(id);

        assertTrue(found.isPresent());
        assertEquals(id, found.get().getId());
        assertEquals(ItemCategory.AVATAR, found.get().getCategory());
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
        ItemDefinition definition = avatar();
        when(springData.findByName("Blue Avatar"))
                .thenReturn(Optional.of(ItemDefinitionJpaMapper.toEntity(definition)));

        Optional<ItemDefinition> found = adapter.findByName("Blue Avatar");

        assertTrue(found.isPresent());
        assertEquals(definition.getId(), found.get().getId());
        verify(springData).findByName("Blue Avatar");
    }

    @Test
    @DisplayName("findByName deve retornar vazio quando não existir")
    void findByNameShouldReturnEmptyWhenMissing() {
        when(springData.findByName("missing")).thenReturn(Optional.empty());

        assertTrue(adapter.findByName("missing").isEmpty());
    }
}
