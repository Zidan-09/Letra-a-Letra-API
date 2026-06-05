package com.letraaletra.api.features.user.domain.stats;

public class UserStats {
    private int totalMatches;
    private int totalWins;
    private int winStreak;
    private int points;

    public UserStats(int totalMatches, int totalWins, int winStreak, int points) {
        this.totalMatches = totalMatches;
        this.totalWins = totalWins;
        this.winStreak = winStreak;
        this.points = points;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getWinStreak() {
        return winStreak;
    }

    public int getPoints() {
        return points;
    }

    public void registerWin() {
        totalMatches++;
        totalWins++;
        winStreak++;
    }

    public void registerLose() {
        totalMatches++;
        winStreak = 0;
    }

    private void incrementPoints(int userPoints, int opponentPoints) {
        points = Math.max(userPoints, opponentPoints) - Math.min(userPoints, opponentPoints);
    }
}
