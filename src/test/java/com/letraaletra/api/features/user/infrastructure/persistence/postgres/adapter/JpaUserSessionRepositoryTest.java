package com.letraaletra.api.features.user.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.user.domain.session.SessionRevocationReason;
import com.letraaletra.api.features.user.domain.session.UserSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaUserSessionRepository.class)
class JpaUserSessionRepositoryTest {

    @Autowired
    private JpaUserSessionRepository repository;

    private UserSession newSession(UUID userId, String currentHash) {
        LocalDateTime now = LocalDateTime.now();
        return UserSession.create(userId, currentHash, now, now.plusDays(90));
    }

    @Test
    @DisplayName("Deve persistir e recuperar a sessao por usuario e por hash do token")
    void saveAndFind_ShouldPersistSession() {
        UUID userId = UUID.randomUUID();
        repository.save(newSession(userId, "hash-a"));

        Optional<UserSession> byUser = repository.findByUserId(userId);
        assertTrue(byUser.isPresent());
        assertEquals("hash-a", byUser.get().getCurrentTokenHash());

        Optional<UserSession> byHash = repository.findByTokenHashForUpdate("hash-a");
        assertTrue(byHash.isPresent());
        assertEquals(userId, byHash.get().getUserId());
    }

    @Test
    @DisplayName("Deve localizar a sessao pelo hash do token anterior")
    void findByTokenHash_ShouldMatchPreviousHash() {
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        UserSession session = UserSession.create(userId, "hash-a", now.minusMinutes(10), now.plusDays(90));
        session.rotate("hash-b", now, now.plusDays(90));
        repository.save(session);

        Optional<UserSession> byPrevious = repository.findByTokenHashForUpdate("hash-a");
        assertTrue(byPrevious.isPresent());
        assertEquals("hash-b", byPrevious.get().getCurrentTokenHash());

        assertTrue(repository.findByTokenHashForUpdate("unknown-hash").isEmpty());
    }

    @Test
    @DisplayName("Deve persistir revogacao e motivo")
    void save_ShouldPersistRevocation() {
        UUID userId = UUID.randomUUID();
        UserSession session = newSession(userId, "hash-a");
        session.revoke(SessionRevocationReason.LOGOUT, LocalDateTime.now());
        repository.save(session);

        Optional<UserSession> reloaded = repository.findByUserId(userId);
        assertTrue(reloaded.isPresent());
        assertTrue(reloaded.get().isRevoked());
        assertEquals(SessionRevocationReason.LOGOUT, reloaded.get().getRevocationReason());
    }

    @Test
    @DisplayName("Deve retornar vazio quando nao existir sessao")
    void find_WhenMissing_ShouldReturnEmpty() {
        assertTrue(repository.findByUserId(UUID.randomUUID()).isEmpty());
        assertFalse(repository.findByTokenHashForUpdate("nope").isPresent());
    }
}
