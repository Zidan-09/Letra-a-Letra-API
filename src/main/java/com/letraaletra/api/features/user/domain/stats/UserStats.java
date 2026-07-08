package com.letraaletra.api.features.user.domain.stats;

public class UserStats {
    private int totalMatches;
    private int totalWins;
    private int winStreak;
    private int level;
    private int experience;
    private int points;

    public UserStats(
            int totalMatches,
            int totalWins,
            int winStreak,
            int level,
            int experience,
            int points
    ) {
        this.totalMatches = totalMatches;
        this.totalWins = totalWins;
        this.winStreak = winStreak;
        this.level = level;
        this.experience = experience;
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

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getPoints() {
        return points;
    }

    public void incrementExperience(int value, int maxLevel) {
        experience += value;

        advanceLevel(maxLevel);
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
        points += Math.max(userPoints, opponentPoints) - Math.min(userPoints, opponentPoints);
    }

    private void advanceLevel(int maxLevel) {
        double multiplier = 20.0;
        double factor = 1.0 / 1.6;

        int newLevel = (int) Math.floor(Math.pow((experience / multiplier), factor));

        level = Math.clamp(newLevel, 1, maxLevel);
    }
}
