package com.letraaletra.api.features.user.domain.stats;

import com.letraaletra.api.features.user.domain.User;
import com.letraaletra.api.features.user.domain.factory.UserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserStatsTest {

    private User user;
    private final UserFactory userFactory = new UserFactory();

    @BeforeEach
    void setUp() {
        user = userFactory.createLocal("Gamer", "game@test.com", "hash");
    }

    @Test
    @DisplayName("Deve incrementar partidas, vitórias e a sequência (streak) ao registrar vitória")
    void shouldTrackStatsCorrectlyOnWin() {
        UserStats stats = user.getStats();

        assertEquals(0, stats.getTotalMatches());
        assertEquals(0, stats.getTotalWins());
        assertEquals(0, stats.getWinStreak());

        user.registerMatchResult(true);
        assertEquals(1, stats.getTotalMatches());
        assertEquals(1, stats.getTotalWins());
        assertEquals(1, stats.getWinStreak());

        user.registerMatchResult(true);
        assertEquals(2, stats.getTotalMatches());
        assertEquals(2, stats.getTotalWins());
        assertEquals(2, stats.getWinStreak());
    }

    @Test
    @DisplayName("Deve incrementar partidas mas zerar o winStreak ao registrar uma derrota")
    void shouldResetWinStreakOnLose() {
        UserStats stats = user.getStats();

        user.registerMatchResult(true);
        user.registerMatchResult(true);
        user.registerMatchResult(true);
        assertEquals(3, stats.getWinStreak());
        assertEquals(3, stats.getTotalWins());

        user.registerMatchResult(false);

        assertEquals(4, stats.getTotalMatches(), "O total de partidas deve subir para 4");
        assertEquals(3, stats.getTotalWins(), "O total de vitórias deve permanecer em 3");
        assertEquals(0, stats.getWinStreak(), "O Win Streak precisa ser reiniciado para 0");
    }
}