package com.letraaletra.api.features.game.infrastructure.persistence.postgres.adapter;

import com.letraaletra.api.features.game.domain.Game;
import com.letraaletra.api.features.game.domain.GameType;
import com.letraaletra.api.features.game.domain.GamesPage;
import com.letraaletra.api.features.game.domain.board.Board;
import com.letraaletra.api.features.game.domain.board.generator.BoardGenerator;
import com.letraaletra.api.features.game.domain.history.GameHistory;
import com.letraaletra.api.features.game.domain.history.SpectatorHistory;
import com.letraaletra.api.features.game.domain.room.RoomSettings;
import com.letraaletra.api.features.game.domain.state.GameMode;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataGameRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataMatchPlayerRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataMatchRepository;
import com.letraaletra.api.features.game.infrastructure.persistence.postgres.jpa.SpringDataMatchSpectatorRepository;
import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.UserFactory;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.jpa.SpringDataUserRepository;
import com.letraaletra.api.features.user.infrastructure.persistence.postgres.mapper.UserProcedureMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaGameRepository.class)
class JpaGameRepositorySpectatorTest {

    @Autowired
    private JpaGameRepository gameRepository;

    @Autowired
    private SpringDataGameRepository springGameRepo;
    @Autowired
    private SpringDataMatchRepository matchRepo;
    @Autowired
    private SpringDataMatchPlayerRepository playerRepo;
    @Autowired
    private SpringDataMatchSpectatorRepository spectatorRepo;
    @Autowired
    private EntityManager em;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    private User newUser(String username) {
        User u = UserFactory.createLocal(username, username + "@test.com", "hash");
        em.createNativeQuery("INSERT INTO \"user\" (user_id, username, email, password_hash, token_version, can_change_nickname, created_at) VALUES (?, ?, ?, ?, ?, true, CURRENT_TIMESTAMP)")
                .setParameter(1, u.getUserId())
                .setParameter(2, u.getUsername())
                .setParameter(3, u.getEmail())
                .setParameter(4, "hash")
                .setParameter(5, UUID.randomUUID())
                .executeUpdate();
        em.createNativeQuery("INSERT INTO \"user_stats\" (user_id, total_matches, total_wins, win_streak, level, experience, ranking_points) VALUES (?, 0,0,0,1,0,0)").setParameter(1, u.getUserId()).executeUpdate();
        em.createNativeQuery("INSERT INTO \"user_wallet\" (user_id, soft_coins, hard_gems) VALUES (?, 0,0)").setParameter(1, u.getUserId()).executeUpdate();
        em.flush();
        return u;
    }

    private Game createGameWithBoardAndSpectators(int spectatorCount, String codePrefix) {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create(codePrefix + UUID.randomUUID().toString().substring(0,4), "room", settings, GameType.CUSTOM);
        // Need host id etc will be set on join
        User host = newUser("host_" + UUID.randomUUID().toString().substring(0,4));
        User p2 = newUser("p2_" + UUID.randomUUID().toString().substring(0,4));
        game.join(host, "s-host");
        game.join(p2, "s-p2");
        for (int i=0;i<spectatorCount;i++) {
            User s = newUser("spec" + i + "_" + UUID.randomUUID().toString().substring(0,3));
            game.join(s, "s-spec-" + i);
        }
        Board board = BoardGenerator.generate(List.of("alpha","beta","gamma","delta","epsilon","zeta","eta","theta"), GameMode.NORMAL);
        game.start(board);
        return game;
    }

    @Test
    @DisplayName("WAITING sem GameState não deve falhar e histórico sem matches")
    void waitingWithoutGameStateShouldNotFail() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODE01", "room-wait", settings, GameType.CUSTOM);
        User host = newUser("waitHost");
        game.join(host, "s-wait");
        // save while WAITING / gameState null
        gameRepository.save(game);
        em.flush(); em.clear();

        var page = gameRepository.get(new GamesPage(0, 10, Sort.unsorted()));
        assertTrue(page.getTotalElements() >= 1);
        // find our game
        GameHistory gh = page.getContent().stream().filter(gh2 -> gh2.roomId().equals(game.getId())).findFirst().orElse(null);
        assertNotNull(gh);
        assertTrue(gh.matches().isEmpty());
    }

    @Test
    @DisplayName("RUNNING com 1 espectador deve persistir e recuperar")
    void runningWithOneSpectatorShouldPersistAndRetrieve() {
        Game game = createGameWithBoardAndSpectators(1, "RUN1");
        gameRepository.save(game);
        em.flush(); em.clear();

        var page = gameRepository.get(new GamesPage(0, 10, Sort.unsorted()));
        GameHistory gh = page.getContent().stream().filter(g -> g.roomId().equals(game.getId())).findFirst().orElseThrow();
        assertEquals(1, gh.matches().size());
        assertEquals(2, gh.matches().getFirst().players().size());
        assertEquals(1, gh.matches().getFirst().spectators().size());
        // nickname check
        assertNotNull(gh.matches().getFirst().spectators().getFirst().nickname());
    }

    @Test
    @DisplayName("RUNNING com múltiplos espectadores (5) deve persistir todos")
    void runningWithMultipleSpectatorsShouldPersistAll() {
        Game game = createGameWithBoardAndSpectators(5, "RUN5");
        gameRepository.save(game);
        em.flush(); em.clear();

        var page = gameRepository.get(new GamesPage(0, 10, Sort.unsorted()));
        GameHistory gh = page.getContent().stream().filter(g -> g.roomId().equals(game.getId())).findFirst().orElseThrow();
        assertEquals(5, gh.matches().getFirst().spectators().size());
    }

    @Test
    @DisplayName("CLOSED deve persistir spectators com endedAt")
    void closedShouldPersistSpectators() {
        Game game = createGameWithBoardAndSpectators(2, "CLOS");
        // simulate closed
        game.setGameStatus(com.letraaletra.api.features.game.domain.GameStatus.CLOSED);
        gameRepository.save(game);
        em.flush(); em.clear();

        var page = gameRepository.get(new GamesPage(0, 10, Sort.unsorted()));
        GameHistory gh = page.getContent().stream().filter(g -> g.roomId().equals(game.getId())).findFirst().orElseThrow();
        assertEquals(1, gh.matches().size());
        assertEquals(2, gh.matches().getFirst().spectators().size());
        assertNotNull(gh.matches().getFirst().finishedAt());
    }

    @Test
    @DisplayName("Partida antiga sem espectadores deve retornar lista vazia, não null")
    void oldMatchWithoutSpectatorsShouldReturnEmptyList() {
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("CODEOLD", "oldRoom", settings, GameType.CUSTOM);
        User host = newUser("oldHost");
        User p2 = newUser("oldP2");
        game.join(host, "s-oldHost");
        game.join(p2, "s-oldP2");
        Board board = BoardGenerator.generate(List.of("a","b","c","d","e","f","g","h"), GameMode.NORMAL);
        game.start(board);
        // save without adding spectators
        gameRepository.save(game);
        em.flush(); em.clear();

        var page = gameRepository.get(new GamesPage(0, 10, Sort.unsorted()));
        GameHistory gh = page.getContent().stream().filter(g -> g.roomId().equals(game.getId())).findFirst().orElseThrow();
        assertEquals(1, gh.matches().size());
        assertNotNull(gh.matches().getFirst().spectators());
        assertTrue(gh.matches().getFirst().spectators().isEmpty());
        assertEquals(2, gh.matches().getFirst().players().size());
    }

    @Test
    @DisplayName("legacySave (jdbcTemplate null) deve persistir spectators")
    void legacySaveShouldPersistSpectators() {
        // Build game as before but use legacy constructor with null jdbcTemplate via new instance
        RoomSettings settings = new RoomSettings(true, false);
        Game game = Game.create("LEGACY1", "legacyRoom", settings, GameType.CUSTOM);
        User host = newUser("legHost_" + UUID.randomUUID().toString().substring(0,3));
        User p2 = newUser("legP2_" + UUID.randomUUID().toString().substring(0,3));
        User spec = newUser("legSpec_" + UUID.randomUUID().toString().substring(0,3));
        game.join(host, "s-legHost");
        game.join(p2, "s-legP2");
        game.join(spec, "s-legSpec");
        Board board = BoardGenerator.generate(List.of("a","b","c","d","e","f","g","h"), GameMode.NORMAL);
        game.start(board);

        // Force legacy path by instantiating without jdbcTemplate (spectatorRepo provided)
        JpaGameRepository legacyRepo = new JpaGameRepository(springGameRepo, matchRepo, playerRepo, spectatorRepo);
        legacyRepo.save(game);
        em.flush(); em.clear();

        var page = legacyRepo.get(new GamesPage(0, 10, Sort.unsorted()));
        GameHistory gh = page.getContent().stream().filter(g -> g.roomId().equals(game.getId())).findFirst().orElseThrow();
        assertEquals(1, gh.matches().getFirst().spectators().size());
        SpectatorHistory sh = gh.matches().getFirst().spectators().getFirst();
        assertEquals(spec.getUserId(), sh.spectatorId());
    }

    @Test
    @DisplayName("Upsert idempotente: salvar duas vezes mesmo game não duplica spectators")
    void upsertShouldBeIdempotent() {
        Game game = createGameWithBoardAndSpectators(2, "IDEMP");
        gameRepository.save(game);
        em.flush();
        gameRepository.save(game); // second save same matchId
        em.flush(); em.clear();

        var page = gameRepository.get(new GamesPage(0, 10, Sort.unsorted()));
        GameHistory gh = page.getContent().stream().filter(g -> g.roomId().equals(game.getId())).findFirst().orElseThrow();
        assertEquals(2, gh.matches().getFirst().spectators().size());
        // Also check raw table count via spectatorRepo
        long count = spectatorRepo.count();
        // at least 2 for this match, but count overall >=2
        assertTrue(count >= 2);
    }
}
