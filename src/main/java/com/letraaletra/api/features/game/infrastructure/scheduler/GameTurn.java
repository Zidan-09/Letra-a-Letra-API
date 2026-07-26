package com.letraaletra.api.features.game.infrastructure.scheduler;

import com.letraaletra.api.features.player.domain.Player;
import org.jspecify.annotations.NonNull;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class GameTurn implements Delayed {
    private final UUID gameId;
    private final UUID matchId;
    private final Player player;
    private final Instant turnEndsAt;
    private final int version;

    public GameTurn(UUID gameId, UUID matchId, Player player, Instant turnEndsAt, int version) {
        this.gameId = gameId;
        this.matchId = matchId;
        this.player = player;
        this.turnEndsAt = turnEndsAt;
        this.version = version;
    }

    public UUID gameId() { return gameId; }
    public UUID matchId() { return matchId; }
    public Player player() { return player; }
    public int version() { return version; }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = turnEndsAt.toEpochMilli() - Instant.now().toEpochMilli();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(@NonNull Delayed other) {
        return Long.compare(
                this.turnEndsAt.toEpochMilli(),
                ((GameTurn) other).turnEndsAt.toEpochMilli()
        );
    }
}
