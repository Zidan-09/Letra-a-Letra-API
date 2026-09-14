package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.inventory.domain.UserItem;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.jpa.SpringDataUserItemRepository;
import com.letraaletra.api.features.inventory.infrastructure.persistence.postgres.mapper.UserItemJpaMapper;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaUserRepository.class)
@DisplayName("Inventory Separation Tests (Etapa 8a2: sem dual-write)")
class InventoryDualWriteTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private SpringDataUserItemRepository userItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("save do usuário não deve tocar em user_item (posse vive no agregado novo)")
    void userSaveShouldNotTouchUserItem() {
        User user = UserFactory.createLocal("separate", "separate@test.com", "hash");

        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        List<UserItem> items = userItemRepository.findItemsByOwner(user.getUserId()).stream()
                .map(UserItemJpaMapper::toDomain)
                .toList();
        assertTrue(items.isEmpty());
        assertTrue(jpaUserRepository.find(user.getUserId()).isPresent());
    }

    @Test
    @DisplayName("posse escrita direto no agregado novo sobrevive independente do usuário")
    void userItemWrittenDirectlySurvivesIndependently() {
        User user = UserFactory.createLocal("owner", "owner@test.com", "hash");
        jpaUserRepository.save(user);

        UUID definitionId = UUID.randomUUID();
        userItemRepository.save(UserItemJpaMapper.toEntity(
                user.getUserId(),
                UserItem.restore(user.getUserId(), definitionId, 2, true, LocalDateTime.now(), null)
        ));
        entityManager.flush();
        entityManager.clear();

        jpaUserRepository.save(user);
        entityManager.flush();
        entityManager.clear();

        List<UserItem> items = userItemRepository.findItemsByOwner(user.getUserId()).stream()
                .map(UserItemJpaMapper::toDomain)
                .toList();
        assertEquals(1, items.size());
        assertEquals(definitionId, items.get(0).getDefinitionId());
        assertEquals(2, items.get(0).getQuantity());
    }
}
