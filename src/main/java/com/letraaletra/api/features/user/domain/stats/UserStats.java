package com.letraaletra.api.features.user.domain.stats;

public class UserStats {
    private int totalMatches;
    private int totalWins;
    private int winStreak;
    private int level;
    private int experience;
    private int rankingPoints;

    private UserStats(
            int totalMatches,
            int totalWins,
            int winStreak,
            int level,
            int experience,
            int rankingPoints
    ) {
        this.totalMatches = totalMatches;
        this.totalWins = totalWins;
        this.winStreak = winStreak;
        this.level = level;
        this.experience = experience;
        this.rankingPoints = rankingPoints;
    }

    public static UserStats create() {
        return new UserStats(
                0,
                0,
                0,
                1,
                0,
                0
        );
    }

    public static UserStats restore(
            int totalMatches,
            int totalWins,
            int winStreak,
            int level,
            int experience,
            int rankingPoints
    ) {
        return new UserStats(
                totalMatches,
                totalWins,
                winStreak,
                level,
                experience,
                rankingPoints
        );
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

    public int getRankingPoints() {
        return rankingPoints;
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

    public int incrementPoints(int userPoints, int opponentPoints) {
        int pointsToIncrement = userPoints == 3 ?
                40 - opponentPoints * 10 :
                -30 + (userPoints * 10) + (userPoints > 0 ? 5 : 0);

        rankingPoints = Math.max(0, rankingPoints + pointsToIncrement);

        return pointsToIncrement;
    }

    private void advanceLevel(int maxLevel) {
        double multiplier = 20.0;
        double factor = 1.0 / 1.6;

        int newLevel = (int) Math.floor(Math.pow((experience / multiplier), factor));

        level = Math.clamp(newLevel, 1, maxLevel);
    }
}
