CREATE TABLE
    "user" (
        "user_id" UUID PRIMARY KEY NOT NULL,
        "username" VARCHAR(15) UNIQUE NOT NULL,
        "email" VARCHAR(50) UNIQUE NOT NULL,
        "password_hash" VARCHAR(100),
        "token_version" UUID NOT NULL,
        "google_id" VARCHAR(100) UNIQUE,
        "can_change_nickname" BOOLEAN DEFAULT TRUE,
        "current_game_id" UUID,
        "created_at" TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        CONSTRAINT check_auth_method CHECK (
            "password_hash" IS NOT NULL
            OR "google_id" IS NOT NULL
        )
    );

CREATE TABLE
    "user_stats" (
        "user_id" UUID PRIMARY KEY REFERENCES "user" ("user_id") ON DELETE CASCADE,
        "total_matches" INTEGER DEFAULT 0,
        "total_wins" INTEGER DEFAULT 0,
        "win_streak" INTEGER DEFAULT 0,
        "level" INTEGER DEFAULT 1,
        "experience" INTEGER DEFAULT 0,
        "ranking_points" INTEGER DEFAULT 0
    );

CREATE TABLE
    "user_wallet" (
        "user_id" UUID PRIMARY KEY REFERENCES "user" ("user_id") ON DELETE CASCADE,
        "soft_coins" BIGINT NOT NULL DEFAULT 0 CHECK ("soft_coins" >= 0),
        "hard_gems" BIGINT NOT NULL DEFAULT 0 CHECK ("hard_gems" >= 0)
    );

CREATE TABLE
    "password_reset_code" (
        "password_reset_code_id" UUID PRIMARY KEY NOT NULL,
        "user_id" UUID REFERENCES "user" ("user_id") ON DELETE CASCADE,
        "code_hash" VARCHAR(100) NOT NULL,
        "used" BOOLEAN DEFAULT FALSE,
        "attempts" INTEGER NOT NULL DEFAULT 0,
        "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        "expires_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE
    "game" (
        "game_id" UUID PRIMARY KEY NOT NULL,
        "host_id" UUID REFERENCES "user" ("user_id"),
        "room_name" VARCHAR(50) NOT NULL,
        "created_by_id" UUID REFERENCES "user" ("user_id"),
        "room_code" VARCHAR(50) NOT NULL,
        "game_type" VARCHAR(50) NOT NULL,
        "allow_spectators" BOOLEAN DEFAULT TRUE,
        "private_game" BOOLEAN DEFAULT FALSE,
        "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        "status" VARCHAR(50)
    );

CREATE TABLE
    "matches" (
        "match_id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        "game_id" UUID NOT NULL REFERENCES "game" ("game_id") ON DELETE CASCADE,
        "game_mode" VARCHAR(50) NOT NULL,
        "started_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        "ended_at" TIMESTAMP
    );

CREATE TABLE
    "match_players" (
        "match_id" UUID NOT NULL REFERENCES "matches" ("match_id") ON DELETE CASCADE,
        "user_id" UUID NOT NULL REFERENCES "user" ("user_id") ON DELETE CASCADE,
        "nickname" VARCHAR(15) NOT NULL,
        "score" INTEGER DEFAULT 0,
        "is_winner" BOOLEAN DEFAULT FALSE,
        PRIMARY KEY ("match_id", "user_id")
    );

CREATE TABLE
    "match_spectators" (
        "match_id" UUID NOT NULL REFERENCES "matches" ("match_id") ON DELETE CASCADE,
        "user_id" UUID NOT NULL REFERENCES "user" ("user_id") ON DELETE CASCADE,
        "nickname" VARCHAR(15) NOT NULL,
        PRIMARY KEY ("match_id", "user_id")
    );

CREATE INDEX idx_match_spectators_user ON "match_spectators" ("user_id");
CREATE INDEX idx_match_spectators_match ON "match_spectators" ("match_id");

CREATE TABLE
    "cosmetic" (
        "cosmetic_id" UUID PRIMARY KEY NOT NULL,
        "name" VARCHAR(50) UNIQUE NOT NULL,
        "type" VARCHAR(50) NOT NULL,
        "asset_path" VARCHAR(50) NOT NULL,
        "version" INTEGER NOT NULL DEFAULT 1,
        "available" BOOLEAN DEFAULT TRUE
    );

CREATE TABLE
    "user_inventory" (
        "user_id" UUID NOT NULL REFERENCES "user" ("user_id") ON DELETE CASCADE,
        "cosmetic_id" UUID NOT NULL REFERENCES "cosmetic" ("cosmetic_id") ON DELETE CASCADE,
        "equipped" BOOLEAN DEFAULT FALSE,
        "unlocked_at" TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY ("user_id", "cosmetic_id")
    );

CREATE TABLE
    "offer" (
        "offer_id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        "title" VARCHAR(100) NOT NULL,
        "coin_type" VARCHAR(50) NOT NULL,
        "price" NUMERIC(10, 2) NOT NULL CHECK ("price" > 0),
        "active" BOOLEAN NOT NULL DEFAULT TRUE,
        "repeatable" BOOLEAN NOT NULL DEFAULT FALSE,
        "has_expiration" BOOLEAN NOT NULL DEFAULT TRUE,
        "expires_at" TIMESTAMPTZ,
        "created_at" TIMESTAMPTZ
    );

CREATE TABLE
    "offer_reward" (
        "offer_reward_id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        "offer_id" UUID NOT NULL REFERENCES "offer" ("offer_id") ON DELETE CASCADE,
        "reward_type" VARCHAR(50) NOT NULL,
        "reward_reference" UUID REFERENCES "cosmetic" ("cosmetic_id") ON DELETE SET NULL,
        "quantity" INTEGER NOT NULL DEFAULT 1
    );

CREATE TABLE
    "transaction" (
        "transaction_id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        "user_id" UUID NOT NULL REFERENCES "user" ("user_id") ON DELETE SET NULL,
        "coin_type" VARCHAR(50) NOT NULL,
        "amount" INTEGER NOT NULL,
        "balance_before" INTEGER NOT NULL,
        "balance_after" INTEGER NOT NULL,
        "operation" VARCHAR(20) NOT NULL,
        "reason" VARCHAR(50) NOT NULL,
        "reference_id" UUID NULL,
        "created_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE
    "friend" (
        "user_id_1" UUID,
        "user_id_2" UUID,
        "status" VARCHAR(50) NOT NULL,
        "request_date" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        PRIMARY KEY ("user_id_1", "user_id_2")
    );

CREATE TABLE
    "admin" (
        "admin_id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        "name" VARCHAR(50) NOT NULL,
        "email" VARCHAR(50) UNIQUE NOT NULL,
        "password_hash" VARCHAR(100),
        "token_version" UUID NOT NULL,
        "is_super" BOOLEAN NOT NULL DEFAULT FALSE,
        "created_at" TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE
    "admin_permission" (
        "admin_id" UUID NOT NULL REFERENCES "admin" ("admin_id") ON DELETE CASCADE,
        "permission_key" VARCHAR(30) NOT NULL,
        "action" VARCHAR(30) NOT NULL,
        PRIMARY KEY ("admin_id", "permission_key", "action")
    );

CREATE TABLE
    "admin_setup_password_token" (
        "token_hash" VARCHAR(100) PRIMARY KEY NOT NULL,
        "admin_id" UUID NOT NULL REFERENCES "admin" ("admin_id") ON DELETE CASCADE,
        "expires_at" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
        "used" BOOLEAN NOT NULL DEFAULT FALSE
    );

CREATE TABLE
    "admin_password_reset_token" (
        "password_reset_token_id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        "admin_id" UUID REFERENCES "admin" ("admin_id") ON DELETE CASCADE,
        "token_hash" VARCHAR(100) NOT NULL,
        "used" BOOLEAN DEFAULT FALSE,
        "attempts" INTEGER NOT NULL DEFAULT 0,
        "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        "expires_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE
    "ban_history" (
        "ban_history_id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        "user_id" UUID REFERENCES "user" ("user_id") ON DELETE SET NULL,
        "admin_id" UUID REFERENCES "admin" ("admin_id") ON DELETE SET NULL,
        "reason" VARCHAR(500) NOT NULL,
        "type" VARCHAR(50) NOT NULL,
        "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        "expires_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        "removed_at" TIMESTAMP,
        "removed_by" UUID REFERENCES "admin" ("admin_id") ON DELETE SET NULL
    );

CREATE TABLE
    "level" (
        "level_id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        "level" INTEGER NOT NULL UNIQUE
    );

CREATE TABLE
    "level_reward" (
        "level_reward_id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        "level_id" UUID NOT NULL REFERENCES "level" ("level_id") ON DELETE CASCADE,
        "reward_type" VARCHAR(50) NOT NULL,
        "reward_reference" UUID REFERENCES "cosmetic" ("cosmetic_id") ON DELETE SET NULL,
        "quantity" INTEGER NOT NULL DEFAULT 1
    );

CREATE INDEX idx_game_room_code_active ON "game" ("room_code")
WHERE
    status = 'WAITING';

CREATE INDEX idx_user_stats_wins ON "user_stats" ("total_wins" DESC);

CREATE INDEX idx_match_players_user ON "match_players" ("user_id");

CREATE TABLE
    "audit_event" (
        "event_id" UUID PRIMARY KEY,
        "occurred_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
        "category" VARCHAR(30) NOT NULL,
        "event_type" VARCHAR(50) NOT NULL,
        "outcome" VARCHAR(10) NOT NULL,
        "failure_reason" TEXT NULL,
        "actor_type" VARCHAR(10) NOT NULL,
        "actor_id" UUID NULL,
        "actor_name" VARCHAR(60) NULL,
        "target_user_id" UUID NULL REFERENCES "user" ("user_id") ON DELETE SET NULL,
        "resource_type" VARCHAR(30) NOT NULL,
        "resource_id" VARCHAR(64) NOT NULL,
        "before_state" JSONB NULL,
        "after_state" JSONB NULL,
        "delta" JSONB NULL,
        "reason_code" VARCHAR(50) NULL,
        "request_id" VARCHAR(64) NULL,
        "operation_id" UUID NULL,
        "correlation_id" VARCHAR(64) NULL,
        "source_type" VARCHAR(20) NOT NULL,
        "source_detail" VARCHAR(200) NULL,
        "transaction_id" UUID NULL,
        "metadata" JSONB NULL
    );

CREATE TABLE
    "ticket" (
        "ticket_id" UUID PRIMARY KEY DEFAULT gen_random_uuid (),
        "user_id" UUID NOT NULL REFERENCES "user" ("user_id"),
        "category" VARCHAR(30) NOT NULL,
        "status" VARCHAR(20) NOT NULL,
        "subject" VARCHAR(100) NOT NULL,
        "description" VARCHAR(4000) NOT NULL,
        "resolution_note" VARCHAR(1000),
        "resolved_by_admin_id" UUID REFERENCES "admin" ("admin_id") ON DELETE SET NULL,
        "resolved_at" TIMESTAMPTZ,
        "created_at" TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

CREATE INDEX idx_audit_user_time ON "audit_event" ("target_user_id", "occurred_at" DESC);

CREATE INDEX idx_audit_resource ON "audit_event" (
    "resource_type",
    "resource_id",
    "occurred_at" DESC
);

CREATE INDEX idx_audit_type_time ON "audit_event" ("event_type", "occurred_at" DESC);

CREATE INDEX idx_audit_operation ON "audit_event" ("operation_id")
WHERE
    "operation_id" IS NOT NULL;

CREATE INDEX idx_audit_request ON "audit_event" ("request_id")
WHERE
    "request_id" IS NOT NULL;

CREATE INDEX idx_audit_transaction ON "audit_event" ("transaction_id")
WHERE
    "transaction_id" IS NOT NULL;

CREATE INDEX idx_ticket_user_created ON "ticket" ("user_id", "created_at" DESC);

CREATE INDEX idx_ticket_status_created ON "ticket" ("status", "created_at" DESC);-- V002__stored_procedures.sql
-- Stored Procedures and Functions for Letra-a-Letra-API
-- Preserves all existing tables, adds atomic operations to reduce round-trips and N+1 queries.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================================
-- SP-01: sp_user_save — atomic persistence of User aggregate
-- Replaces 4-6 statements (user + stats + wallet + delete+insert inventory) with 1 CALL.
-- ============================================================================
CREATE OR REPLACE PROCEDURE sp_user_save(
    p_user_id UUID,
    p_username VARCHAR(15),
    p_email VARCHAR(50),
    p_password_hash VARCHAR(100),
    p_token_version UUID,
    p_google_id VARCHAR(100),
    p_can_change_nickname BOOLEAN,
    p_current_game_id UUID,
    p_created_at TIMESTAMPTZ,
    p_total_matches INT,
    p_total_wins INT,
    p_win_streak INT,
    p_level INT,
    p_experience INT,
    p_ranking_points INT,
    p_soft_coins BIGINT,
    p_hard_gems BIGINT,
    p_inventory JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO "user" (user_id, username, email, password_hash, token_version, google_id, can_change_nickname, current_game_id, created_at)
    VALUES (p_user_id, p_username, p_email, p_password_hash, p_token_version, p_google_id, p_can_change_nickname, p_current_game_id, COALESCE(p_created_at, NOW()))
    ON CONFLICT (user_id) DO UPDATE SET
        username = EXCLUDED.username,
        email = EXCLUDED.email,
        password_hash = EXCLUDED.password_hash,
        token_version = EXCLUDED.token_version,
        google_id = EXCLUDED.google_id,
        can_change_nickname = EXCLUDED.can_change_nickname,
        current_game_id = EXCLUDED.current_game_id;

    INSERT INTO user_stats (user_id, total_matches, total_wins, win_streak, level, experience, ranking_points)
    VALUES (p_user_id, p_total_matches, p_total_wins, p_win_streak, p_level, p_experience, p_ranking_points)
    ON CONFLICT (user_id) DO UPDATE SET
        total_matches = EXCLUDED.total_matches,
        total_wins = EXCLUDED.total_wins,
        win_streak = EXCLUDED.win_streak,
        level = EXCLUDED.level,
        experience = EXCLUDED.experience,
        ranking_points = EXCLUDED.ranking_points;

    INSERT INTO user_wallet (user_id, soft_coins, hard_gems)
    VALUES (p_user_id, p_soft_coins, p_hard_gems)
    ON CONFLICT (user_id) DO UPDATE SET
        soft_coins = EXCLUDED.soft_coins,
        hard_gems = EXCLUDED.hard_gems;

    DELETE FROM user_inventory WHERE user_id = p_user_id;

    IF p_inventory IS NOT NULL AND jsonb_array_length(p_inventory) > 0 THEN
        INSERT INTO user_inventory (user_id, cosmetic_id, equipped, unlocked_at)
        SELECT
            p_user_id,
            (elem->>'cosmetic_id')::UUID,
            COALESCE((elem->>'equipped')::BOOLEAN, FALSE),
            COALESCE((elem->>'unlocked_at')::TIMESTAMPTZ, NOW())
        FROM jsonb_array_elements(p_inventory) AS elem;
    END IF;
END;
$$;

-- ============================================================================
-- SP-02: User read functions — single query with JSON aggregation for inventory
-- Each function returns 1 row per user with inventory JSONB.
-- ============================================================================

-- Helper: returns user details with inventory aggregated
-- Used by all find functions below

CREATE OR REPLACE FUNCTION sp_user_find_by_id(p_user_id UUID)
RETURNS TABLE(
    user_id UUID,
    username VARCHAR(15),
    email VARCHAR(50),
    password_hash VARCHAR(100),
    google_id VARCHAR(100),
    token_version UUID,
    current_game_id UUID,
    can_change_nickname BOOLEAN,
    ban_type VARCHAR(50),
    ban_reason VARCHAR(500),
    ban_expires_at TIMESTAMP,
    total_matches INT,
    total_wins INT,
    win_streak INT,
    level INT,
    experience INT,
    ranking_points INT,
    soft_coins BIGINT,
    hard_gems BIGINT,
    created_at TIMESTAMPTZ,
    inventory JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        u.user_id,
        u.username,
        u.email,
        u.password_hash,
        u.google_id,
        u.token_version,
        u.current_game_id,
        u.can_change_nickname,
        b.type::VARCHAR(50) AS ban_type,
        b.reason::VARCHAR(500) AS ban_reason,
        b.expires_at AS ban_expires_at,
        s.total_matches,
        s.total_wins,
        s.win_streak,
        s.level,
        s.experience,
        s.ranking_points,
        w.soft_coins,
        w.hard_gems,
        u.created_at,
        COALESCE(inv.inventory, '[]'::JSONB) AS inventory
    FROM "user" u
    JOIN user_stats s ON s.user_id = u.user_id
    JOIN user_wallet w ON w.user_id = u.user_id
    LEFT JOIN ban_history b ON b.user_id = u.user_id
        AND b.removed_at IS NULL
        AND (b.expires_at IS NULL OR b.expires_at > NOW())
    LEFT JOIN LATERAL (
        SELECT jsonb_agg(jsonb_build_object(
            'cosmetic_id', c.cosmetic_id,
            'name', c.name,
            'type', c.type,
            'equipped', ui.equipped,
            'unlocked_at', ui.unlocked_at,
            'user_id', ui.user_id
        )) AS inventory
        FROM user_inventory ui
        JOIN cosmetic c ON c.cosmetic_id = ui.cosmetic_id
        WHERE ui.user_id = u.user_id
    ) inv ON true
    WHERE u.user_id = p_user_id;
END;
$$;

CREATE OR REPLACE FUNCTION sp_user_find_by_username(p_username VARCHAR)
RETURNS TABLE(
    user_id UUID,
    username VARCHAR(15),
    email VARCHAR(50),
    password_hash VARCHAR(100),
    google_id VARCHAR(100),
    token_version UUID,
    current_game_id UUID,
    can_change_nickname BOOLEAN,
    ban_type VARCHAR(50),
    ban_reason VARCHAR(500),
    ban_expires_at TIMESTAMP,
    total_matches INT,
    total_wins INT,
    win_streak INT,
    level INT,
    experience INT,
    ranking_points INT,
    soft_coins BIGINT,
    hard_gems BIGINT,
    created_at TIMESTAMPTZ,
    inventory JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        u.user_id,
        u.username,
        u.email,
        u.password_hash,
        u.google_id,
        u.token_version,
        u.current_game_id,
        u.can_change_nickname,
        b.type::VARCHAR(50),
        b.reason::VARCHAR(500),
        b.expires_at,
        s.total_matches,
        s.total_wins,
        s.win_streak,
        s.level,
        s.experience,
        s.ranking_points,
        w.soft_coins,
        w.hard_gems,
        u.created_at,
        COALESCE(inv.inventory, '[]'::JSONB)
    FROM "user" u
    JOIN user_stats s ON s.user_id = u.user_id
    JOIN user_wallet w ON w.user_id = u.user_id
    LEFT JOIN ban_history b ON b.user_id = u.user_id
        AND b.removed_at IS NULL
        AND (b.expires_at IS NULL OR b.expires_at > NOW())
    LEFT JOIN LATERAL (
        SELECT jsonb_agg(jsonb_build_object(
            'cosmetic_id', c.cosmetic_id,
            'name', c.name,
            'type', c.type,
            'equipped', ui.equipped,
            'unlocked_at', ui.unlocked_at,
            'user_id', ui.user_id
        )) AS inventory
        FROM user_inventory ui
        JOIN cosmetic c ON c.cosmetic_id = ui.cosmetic_id
        WHERE ui.user_id = u.user_id
    ) inv ON true
    WHERE u.username = p_username;
END;
$$;

CREATE OR REPLACE FUNCTION sp_user_find_by_email(p_email VARCHAR)
RETURNS TABLE(
    user_id UUID,
    username VARCHAR(15),
    email VARCHAR(50),
    password_hash VARCHAR(100),
    google_id VARCHAR(100),
    token_version UUID,
    current_game_id UUID,
    can_change_nickname BOOLEAN,
    ban_type VARCHAR(50),
    ban_reason VARCHAR(500),
    ban_expires_at TIMESTAMP,
    total_matches INT,
    total_wins INT,
    win_streak INT,
    level INT,
    experience INT,
    ranking_points INT,
    soft_coins BIGINT,
    hard_gems BIGINT,
    created_at TIMESTAMPTZ,
    inventory JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        u.user_id,
        u.username,
        u.email,
        u.password_hash,
        u.google_id,
        u.token_version,
        u.current_game_id,
        u.can_change_nickname,
        b.type::VARCHAR(50),
        b.reason::VARCHAR(500),
        b.expires_at,
        s.total_matches,
        s.total_wins,
        s.win_streak,
        s.level,
        s.experience,
        s.ranking_points,
        w.soft_coins,
        w.hard_gems,
        u.created_at,
        COALESCE(inv.inventory, '[]'::JSONB)
    FROM "user" u
    JOIN user_stats s ON s.user_id = u.user_id
    JOIN user_wallet w ON w.user_id = u.user_id
    LEFT JOIN ban_history b ON b.user_id = u.user_id
        AND b.removed_at IS NULL
        AND (b.expires_at IS NULL OR b.expires_at > NOW())
    LEFT JOIN LATERAL (
        SELECT jsonb_agg(jsonb_build_object(
            'cosmetic_id', c.cosmetic_id,
            'name', c.name,
            'type', c.type,
            'equipped', ui.equipped,
            'unlocked_at', ui.unlocked_at,
            'user_id', ui.user_id
        )) AS inventory
        FROM user_inventory ui
        JOIN cosmetic c ON c.cosmetic_id = ui.cosmetic_id
        WHERE ui.user_id = u.user_id
    ) inv ON true
    WHERE u.email = p_email;
END;
$$;

CREATE OR REPLACE FUNCTION sp_user_find_by_google_id(p_google_id VARCHAR)
RETURNS TABLE(
    user_id UUID,
    username VARCHAR(15),
    email VARCHAR(50),
    password_hash VARCHAR(100),
    google_id VARCHAR(100),
    token_version UUID,
    current_game_id UUID,
    can_change_nickname BOOLEAN,
    ban_type VARCHAR(50),
    ban_reason VARCHAR(500),
    ban_expires_at TIMESTAMP,
    total_matches INT,
    total_wins INT,
    win_streak INT,
    level INT,
    experience INT,
    ranking_points INT,
    soft_coins BIGINT,
    hard_gems BIGINT,
    created_at TIMESTAMPTZ,
    inventory JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        u.user_id,
        u.username,
        u.email,
        u.password_hash,
        u.google_id,
        u.token_version,
        u.current_game_id,
        u.can_change_nickname,
        b.type::VARCHAR(50),
        b.reason::VARCHAR(500),
        b.expires_at,
        s.total_matches,
        s.total_wins,
        s.win_streak,
        s.level,
        s.experience,
        s.ranking_points,
        w.soft_coins,
        w.hard_gems,
        u.created_at,
        COALESCE(inv.inventory, '[]'::JSONB)
    FROM "user" u
    JOIN user_stats s ON s.user_id = u.user_id
    JOIN user_wallet w ON w.user_id = u.user_id
    LEFT JOIN ban_history b ON b.user_id = u.user_id
        AND b.removed_at IS NULL
        AND (b.expires_at IS NULL OR b.expires_at > NOW())
    LEFT JOIN LATERAL (
        SELECT jsonb_agg(jsonb_build_object(
            'cosmetic_id', c.cosmetic_id,
            'name', c.name,
            'type', c.type,
            'equipped', ui.equipped,
            'unlocked_at', ui.unlocked_at,
            'user_id', ui.user_id
        )) AS inventory
        FROM user_inventory ui
        JOIN cosmetic c ON c.cosmetic_id = ui.cosmetic_id
        WHERE ui.user_id = u.user_id
    ) inv ON true
    WHERE u.google_id = p_google_id;
END;
$$;

-- Batch find by IDs — returns multiple rows
CREATE OR REPLACE FUNCTION sp_user_find_by_ids(p_user_ids UUID[])
RETURNS TABLE(
    user_id UUID,
    username VARCHAR(15),
    email VARCHAR(50),
    password_hash VARCHAR(100),
    google_id VARCHAR(100),
    token_version UUID,
    current_game_id UUID,
    can_change_nickname BOOLEAN,
    ban_type VARCHAR(50),
    ban_reason VARCHAR(500),
    ban_expires_at TIMESTAMP,
    total_matches INT,
    total_wins INT,
    win_streak INT,
    level INT,
    experience INT,
    ranking_points INT,
    soft_coins BIGINT,
    hard_gems BIGINT,
    created_at TIMESTAMPTZ,
    inventory JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        u.user_id,
        u.username,
        u.email,
        u.password_hash,
        u.google_id,
        u.token_version,
        u.current_game_id,
        u.can_change_nickname,
        b.type::VARCHAR(50),
        b.reason::VARCHAR(500),
        b.expires_at,
        s.total_matches,
        s.total_wins,
        s.win_streak,
        s.level,
        s.experience,
        s.ranking_points,
        w.soft_coins,
        w.hard_gems,
        u.created_at,
        COALESCE(inv.inventory, '[]'::JSONB)
    FROM "user" u
    JOIN user_stats s ON s.user_id = u.user_id
    JOIN user_wallet w ON w.user_id = u.user_id
    LEFT JOIN ban_history b ON b.user_id = u.user_id
        AND b.removed_at IS NULL
        AND (b.expires_at IS NULL OR b.expires_at > NOW())
    LEFT JOIN LATERAL (
        SELECT jsonb_agg(jsonb_build_object(
            'cosmetic_id', c.cosmetic_id,
            'name', c.name,
            'type', c.type,
            'equipped', ui.equipped,
            'unlocked_at', ui.unlocked_at,
            'user_id', ui.user_id
        )) AS inventory
        FROM user_inventory ui
        JOIN cosmetic c ON c.cosmetic_id = ui.cosmetic_id
        WHERE ui.user_id = u.user_id
    ) inv ON true
    WHERE u.user_id = ANY(p_user_ids);
END;
$$;

-- Paged find — for GetUsersUseCase
CREATE OR REPLACE FUNCTION sp_user_find_page(
    p_limit INT,
    p_offset INT
)
RETURNS TABLE(
    user_id UUID,
    username VARCHAR(15),
    email VARCHAR(50),
    password_hash VARCHAR(100),
    google_id VARCHAR(100),
    token_version UUID,
    current_game_id UUID,
    can_change_nickname BOOLEAN,
    ban_type VARCHAR(50),
    ban_reason VARCHAR(500),
    ban_expires_at TIMESTAMP,
    total_matches INT,
    total_wins INT,
    win_streak INT,
    level INT,
    experience INT,
    ranking_points INT,
    soft_coins BIGINT,
    hard_gems BIGINT,
    created_at TIMESTAMPTZ,
    inventory JSONB,
    total_count BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        u.user_id,
        u.username,
        u.email,
        u.password_hash,
        u.google_id,
        u.token_version,
        u.current_game_id,
        u.can_change_nickname,
        b.type::VARCHAR(50),
        b.reason::VARCHAR(500),
        b.expires_at,
        s.total_matches,
        s.total_wins,
        s.win_streak,
        s.level,
        s.experience,
        s.ranking_points,
        w.soft_coins,
        w.hard_gems,
        u.created_at,
        COALESCE(inv.inventory, '[]'::JSONB),
        COUNT(*) OVER() AS total_count
    FROM "user" u
    JOIN user_stats s ON s.user_id = u.user_id
    JOIN user_wallet w ON w.user_id = u.user_id
    LEFT JOIN ban_history b ON b.user_id = u.user_id
        AND b.removed_at IS NULL
        AND (b.expires_at IS NULL OR b.expires_at > NOW())
    LEFT JOIN LATERAL (
        SELECT jsonb_agg(jsonb_build_object(
            'cosmetic_id', c.cosmetic_id,
            'name', c.name,
            'type', c.type,
            'equipped', ui.equipped,
            'unlocked_at', ui.unlocked_at,
            'user_id', ui.user_id
        )) AS inventory
        FROM user_inventory ui
        JOIN cosmetic c ON c.cosmetic_id = ui.cosmetic_id
        WHERE ui.user_id = u.user_id
    ) inv ON true
    ORDER BY u.created_at ASC
    LIMIT p_limit OFFSET p_offset;
END;
$$;

-- ============================================================================
-- SP-03: Offer read functions — eliminate N+1 for rewards + cosmetics
-- ============================================================================

CREATE OR REPLACE FUNCTION sp_offer_find_by_id(p_offer_id UUID)
RETURNS TABLE(
    offer_id UUID,
    title VARCHAR(100),
    coin_type VARCHAR(50),
    price NUMERIC(10,2),
    active BOOLEAN,
    repeatable BOOLEAN,
    has_expiration BOOLEAN,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ,
    rewards JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        o.offer_id,
        o.title,
        o.coin_type,
        o.price,
        o.active,
        o.repeatable,
        o.has_expiration,
        o.expires_at,
        o.created_at,
        COALESCE(
            (SELECT jsonb_agg(jsonb_build_object(
                'offer_reward_id', orw.offer_reward_id,
                'offer_id', orw.offer_id,
                'reward_type', orw.reward_type,
                'reward_reference', orw.reward_reference,
                'quantity', orw.quantity,
                'cosmetic', CASE WHEN orw.reward_type = 'COSMETIC' THEN
                    jsonb_build_object(
                        'cosmetic_id', c.cosmetic_id,
                        'name', c.name,
                        'type', c.type,
                        'asset_path', c.asset_path,
                        'version', c.version,
                        'available', c.available
                    ) ELSE NULL END
            ))
            FROM offer_reward orw
            LEFT JOIN cosmetic c ON c.cosmetic_id = orw.reward_reference AND orw.reward_type = 'COSMETIC'
            WHERE orw.offer_id = o.offer_id),
            '[]'::JSONB
        ) AS rewards
    FROM offer o
    WHERE o.offer_id = p_offer_id;
END;
$$;

CREATE OR REPLACE FUNCTION sp_offer_find_active()
RETURNS TABLE(
    offer_id UUID,
    title VARCHAR(100),
    coin_type VARCHAR(50),
    price NUMERIC(10,2),
    active BOOLEAN,
    repeatable BOOLEAN,
    has_expiration BOOLEAN,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ,
    rewards JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        o.offer_id,
        o.title,
        o.coin_type,
        o.price,
        o.active,
        o.repeatable,
        o.has_expiration,
        o.expires_at,
        o.created_at,
        COALESCE(
            (SELECT jsonb_agg(jsonb_build_object(
                'offer_reward_id', orw.offer_reward_id,
                'offer_id', orw.offer_id,
                'reward_type', orw.reward_type,
                'reward_reference', orw.reward_reference,
                'quantity', orw.quantity,
                'cosmetic', CASE WHEN orw.reward_type = 'COSMETIC' THEN
                    jsonb_build_object(
                        'cosmetic_id', c.cosmetic_id,
                        'name', c.name,
                        'type', c.type,
                        'asset_path', c.asset_path,
                        'version', c.version,
                        'available', c.available
                    ) ELSE NULL END
            ))
            FROM offer_reward orw
            LEFT JOIN cosmetic c ON c.cosmetic_id = orw.reward_reference AND orw.reward_type = 'COSMETIC'
            WHERE orw.offer_id = o.offer_id),
            '[]'::JSONB
        ) AS rewards
    FROM offer o
    WHERE o.active = TRUE;
END;
$$;

CREATE OR REPLACE FUNCTION sp_offer_find_page(p_limit INT, p_offset INT)
RETURNS TABLE(
    offer_id UUID,
    title VARCHAR(100),
    coin_type VARCHAR(50),
    price NUMERIC(10,2),
    active BOOLEAN,
    repeatable BOOLEAN,
    has_expiration BOOLEAN,
    expires_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ,
    rewards JSONB,
    total_count BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        o.offer_id,
        o.title,
        o.coin_type,
        o.price,
        o.active,
        o.repeatable,
        o.has_expiration,
        o.expires_at,
        o.created_at,
        COALESCE(
            (SELECT jsonb_agg(jsonb_build_object(
                'offer_reward_id', orw.offer_reward_id,
                'offer_id', orw.offer_id,
                'reward_type', orw.reward_type,
                'reward_reference', orw.reward_reference,
                'quantity', orw.quantity,
                'cosmetic', CASE WHEN orw.reward_type = 'COSMETIC' THEN
                    jsonb_build_object(
                        'cosmetic_id', c.cosmetic_id,
                        'name', c.name,
                        'type', c.type,
                        'asset_path', c.asset_path,
                        'version', c.version,
                        'available', c.available
                    ) ELSE NULL END
            ))
            FROM offer_reward orw
            LEFT JOIN cosmetic c ON c.cosmetic_id = orw.reward_reference AND orw.reward_type = 'COSMETIC'
            WHERE orw.offer_id = o.offer_id),
            '[]'::JSONB
        ) AS rewards,
        COUNT(*) OVER() AS total_count
    FROM offer o
    ORDER BY o.created_at ASC
    LIMIT p_limit OFFSET p_offset;
END;
$$;

-- ============================================================================
-- SP-03b: Level read functions — same pattern as Offer
-- ============================================================================

CREATE OR REPLACE FUNCTION sp_level_find_by_id(p_level_id UUID)
RETURNS TABLE(
    level_id UUID,
    level_value INT,
    rewards JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        l.level_id,
        l.level AS level_value,
        COALESCE(
            (SELECT jsonb_agg(jsonb_build_object(
                'level_reward_id', lrw.level_reward_id,
                'level_id', lrw.level_id,
                'reward_type', lrw.reward_type,
                'reward_reference', lrw.reward_reference,
                'quantity', lrw.quantity,
                'cosmetic', CASE WHEN lrw.reward_type = 'COSMETIC' THEN
                    jsonb_build_object(
                        'cosmetic_id', c.cosmetic_id,
                        'name', c.name,
                        'type', c.type,
                        'asset_path', c.asset_path,
                        'version', c.version,
                        'available', c.available
                    ) ELSE NULL END
            ))
            FROM level_reward lrw
            LEFT JOIN cosmetic c ON c.cosmetic_id = lrw.reward_reference AND lrw.reward_type = 'COSMETIC'
            WHERE lrw.level_id = l.level_id),
            '[]'::JSONB
        ) AS rewards
    FROM level l
    WHERE l.level_id = p_level_id;
END;
$$;

CREATE OR REPLACE FUNCTION sp_level_find_by_value(p_level_value INT)
RETURNS TABLE(
    level_id UUID,
    level_value INT,
    rewards JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        l.level_id,
        l.level AS level_value,
        COALESCE(
            (SELECT jsonb_agg(jsonb_build_object(
                'level_reward_id', lrw.level_reward_id,
                'level_id', lrw.level_id,
                'reward_type', lrw.reward_type,
                'reward_reference', lrw.reward_reference,
                'quantity', lrw.quantity,
                'cosmetic', CASE WHEN lrw.reward_type = 'COSMETIC' THEN
                    jsonb_build_object(
                        'cosmetic_id', c.cosmetic_id,
                        'name', c.name,
                        'type', c.type,
                        'asset_path', c.asset_path,
                        'version', c.version,
                        'available', c.available
                    ) ELSE NULL END
            ))
            FROM level_reward lrw
            LEFT JOIN cosmetic c ON c.cosmetic_id = lrw.reward_reference AND lrw.reward_type = 'COSMETIC'
            WHERE lrw.level_id = l.level_id),
            '[]'::JSONB
        ) AS rewards
    FROM level l
    WHERE l.level = p_level_value;
END;
$$;

CREATE OR REPLACE FUNCTION sp_level_find_page(p_limit INT, p_offset INT)
RETURNS TABLE(
    level_id UUID,
    level_value INT,
    rewards JSONB,
    total_count BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        l.level_id,
        l.level AS level_value,
        COALESCE(
            (SELECT jsonb_agg(jsonb_build_object(
                'level_reward_id', lrw.level_reward_id,
                'level_id', lrw.level_id,
                'reward_type', lrw.reward_type,
                'reward_reference', lrw.reward_reference,
                'quantity', lrw.quantity,
                'cosmetic', CASE WHEN lrw.reward_type = 'COSMETIC' THEN
                    jsonb_build_object(
                        'cosmetic_id', c.cosmetic_id,
                        'name', c.name,
                        'type', c.type,
                        'asset_path', c.asset_path,
                        'version', c.version,
                        'available', c.available
                    ) ELSE NULL END
            ))
            FROM level_reward lrw
            LEFT JOIN cosmetic c ON c.cosmetic_id = lrw.reward_reference AND lrw.reward_type = 'COSMETIC'
            WHERE lrw.level_id = l.level_id),
            '[]'::JSONB
        ) AS rewards,
        COUNT(*) OVER() AS total_count
    FROM level l
    ORDER BY l.level ASC
    LIMIT p_limit OFFSET p_offset;
END;
$$;

CREATE OR REPLACE FUNCTION sp_level_find_biggest()
RETURNS INT
LANGUAGE plpgsql AS $$
DECLARE v_max INT;
BEGIN
    SELECT MAX(level) INTO v_max FROM level;
    RETURN COALESCE(v_max, 0);
END;
$$;

-- ============================================================================
-- SP-04: sp_offer_save / sp_level_save — atomic save with rewards
-- ============================================================================

CREATE OR REPLACE PROCEDURE sp_offer_save(
    p_offer_id UUID,
    p_title VARCHAR(100),
    p_coin_type VARCHAR(50),
    p_price NUMERIC(10,2),
    p_active BOOLEAN,
    p_repeatable BOOLEAN,
    p_has_expiration BOOLEAN,
    p_expires_at TIMESTAMPTZ,
    p_created_at TIMESTAMPTZ,
    p_rewards JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO offer (offer_id, title, coin_type, price, active, repeatable, has_expiration, expires_at, created_at)
    VALUES (p_offer_id, p_title, p_coin_type, p_price, p_active, p_repeatable, p_has_expiration, p_expires_at, p_created_at)
    ON CONFLICT (offer_id) DO UPDATE SET
        title = EXCLUDED.title,
        coin_type = EXCLUDED.coin_type,
        price = EXCLUDED.price,
        active = EXCLUDED.active,
        repeatable = EXCLUDED.repeatable,
        has_expiration = EXCLUDED.has_expiration,
        expires_at = EXCLUDED.expires_at,
        created_at = EXCLUDED.created_at;

    DELETE FROM offer_reward WHERE offer_id = p_offer_id;

    IF p_rewards IS NOT NULL AND jsonb_array_length(p_rewards) > 0 THEN
        INSERT INTO offer_reward (offer_reward_id, offer_id, reward_type, reward_reference, quantity)
        SELECT
            COALESCE((elem->>'offer_reward_id')::UUID, gen_random_uuid()),
            p_offer_id,
            elem->>'reward_type',
            CASE WHEN elem->>'reward_reference' IS NOT NULL AND elem->>'reward_reference' != 'null' AND elem->>'reward_reference' != ''
                 THEN (elem->>'reward_reference')::UUID ELSE NULL END,
            COALESCE((elem->>'quantity')::INT, 1)
        FROM jsonb_array_elements(p_rewards) AS elem;
    END IF;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_level_save(
    p_level_id UUID,
    p_level_value INT,
    p_rewards JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO level (level_id, level)
    VALUES (p_level_id, p_level_value)
    ON CONFLICT (level_id) DO UPDATE SET
        level = EXCLUDED.level;

    DELETE FROM level_reward WHERE level_id = p_level_id;

    IF p_rewards IS NOT NULL AND jsonb_array_length(p_rewards) > 0 THEN
        INSERT INTO level_reward (level_reward_id, level_id, reward_type, reward_reference, quantity)
        SELECT
            COALESCE((elem->>'level_reward_id')::UUID, gen_random_uuid()),
            p_level_id,
            elem->>'reward_type',
            CASE WHEN elem->>'reward_reference' IS NOT NULL AND elem->>'reward_reference' != 'null' AND elem->>'reward_reference' != ''
                 THEN (elem->>'reward_reference')::UUID ELSE NULL END,
            COALESCE((elem->>'quantity')::INT, 1)
        FROM jsonb_array_elements(p_rewards) AS elem;
    END IF;
END;
$$;

-- ============================================================================
-- SP-05: sp_game_save — atomic game + matches + match_players + match_spectators
-- Preserves conditional ended_at logic: ended_at = NULL if status = RUNNING
-- ============================================================================

DROP PROCEDURE IF EXISTS sp_game_save(UUID, UUID, VARCHAR, UUID, VARCHAR, VARCHAR, VARCHAR, UUID, VARCHAR, TIMESTAMP, JSONB);

CREATE OR REPLACE PROCEDURE sp_game_save(
    p_game_id UUID,
    p_host_id UUID,
    p_room_name VARCHAR(50),
    p_created_by_id UUID,
    p_room_code VARCHAR(50),
    p_game_type VARCHAR(50),
    p_status VARCHAR(50),
    p_match_id UUID,
    p_game_mode VARCHAR(50),
    p_ended_at TIMESTAMP,
    p_players JSONB,
    p_spectators JSONB DEFAULT '[]'::JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO game (game_id, host_id, room_name, created_by_id, room_code, game_type, status)
    VALUES (p_game_id, p_host_id, p_room_name, p_created_by_id, p_room_code, p_game_type::VARCHAR, p_status::VARCHAR)
    ON CONFLICT (game_id) DO UPDATE SET
        host_id = EXCLUDED.host_id,
        room_name = EXCLUDED.room_name,
        created_by_id = EXCLUDED.created_by_id,
        room_code = EXCLUDED.room_code,
        game_type = EXCLUDED.game_type,
        status = EXCLUDED.status;

    IF p_match_id IS NOT NULL THEN
        INSERT INTO matches (match_id, game_id, game_mode, ended_at)
        VALUES (p_match_id, p_game_id, p_game_mode, p_ended_at)
        ON CONFLICT (match_id) DO UPDATE SET
            game_id = EXCLUDED.game_id,
            game_mode = EXCLUDED.game_mode,
            ended_at = EXCLUDED.ended_at;

        IF p_players IS NOT NULL AND jsonb_array_length(p_players) > 0 THEN
            INSERT INTO match_players (match_id, user_id, nickname, score, is_winner)
            SELECT
                p_match_id,
                (elem->>'user_id')::UUID,
                elem->>'nickname',
                COALESCE((elem->>'score')::INT, 0),
                COALESCE((elem->>'is_winner')::BOOLEAN, FALSE)
            FROM jsonb_array_elements(p_players) AS elem
            ON CONFLICT (match_id, user_id) DO UPDATE SET
                nickname = EXCLUDED.nickname,
                score = EXCLUDED.score,
                is_winner = EXCLUDED.is_winner;
        END IF;

        IF p_spectators IS NOT NULL AND jsonb_array_length(p_spectators) > 0 THEN
            INSERT INTO match_spectators (match_id, user_id, nickname)
            SELECT
                p_match_id,
                (elem->>'user_id')::UUID,
                elem->>'nickname'
            FROM jsonb_array_elements(p_spectators) AS elem
            ON CONFLICT (match_id, user_id) DO UPDATE SET
                nickname = EXCLUDED.nickname;
        END IF;
    END IF;
END;
$$;

-- ============================================================================
-- SP-06: sp_buy_offer — atomic purchase with validation, wallet debit, rewards, transactions
-- Preserves all BuyOfferUseCase validations: active, not REAL, not already purchased, balance
-- Uses FOR UPDATE on user_wallet to prevent race.
-- Returns transaction IDs and new balances via OUT params encoded as JSONB return
-- ============================================================================

CREATE OR REPLACE FUNCTION sp_buy_offer(
    p_user_id UUID,
    p_offer_id UUID
)
RETURNS JSONB
LANGUAGE plpgsql AS $$
DECLARE
    v_coin_type VARCHAR(50);
    v_price NUMERIC(10,2);
    v_active BOOLEAN;
    v_repeatable BOOLEAN;
    v_title VARCHAR(100);
    v_soft_coins BIGINT;
    v_hard_gems BIGINT;
    v_price_int INT;
    v_balance_before INT;
    v_balance_after INT;
    v_transaction_id UUID;
    v_new_soft BIGINT;
    v_new_hard BIGINT;
    v_rewards JSONB;
    v_elem JSONB;
    v_reward_type VARCHAR(50);
    v_reward_ref UUID;
    v_quantity INT;
    v_cosmetic_exists BOOLEAN;
    v_result JSONB;
    v_transaction_ids JSONB := '[]'::JSONB;
BEGIN
    -- Lock and validate offer
    SELECT coin_type, price, active, repeatable, title
    INTO v_coin_type, v_price, v_active, v_repeatable, v_title
    FROM offer WHERE offer_id = p_offer_id FOR SHARE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'OfferNotFound' USING ERRCODE = 'P0001';
    END IF;

    IF NOT v_active THEN
        RAISE EXCEPTION 'InvalidOfferStatus' USING ERRCODE = 'P0002';
    END IF;

    IF v_coin_type = 'REAL' THEN
        RAISE EXCEPTION 'InvalidPayment' USING ERRCODE = 'P0003';
    END IF;

    IF NOT v_repeatable THEN
        IF EXISTS (SELECT 1 FROM transaction WHERE user_id = p_user_id AND reference_id = p_offer_id AND reason = 'SHOP_PURCHASE') THEN
            RAISE EXCEPTION 'OfferAlreadyPurchased' USING ERRCODE = 'P0004';
        END IF;
    END IF;

    -- Lock wallet
    SELECT soft_coins, hard_gems INTO v_soft_coins, v_hard_gems
    FROM user_wallet WHERE user_id = p_user_id FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'UserNotFound' USING ERRCODE = 'P0005';
    END IF;

    v_price_int := v_price::INT;

    IF v_coin_type = 'SOFT' THEN
        IF v_soft_coins < v_price_int THEN
            RAISE EXCEPTION 'InsufficientBalance' USING ERRCODE = 'P0006';
        END IF;
        v_balance_before := v_soft_coins::INT;
        UPDATE user_wallet SET soft_coins = soft_coins - v_price_int WHERE user_id = p_user_id;
        v_balance_after := v_balance_before - v_price_int;
    ELSIF v_coin_type = 'HARD' THEN
        IF v_hard_gems < v_price_int THEN
            RAISE EXCEPTION 'InsufficientBalance' USING ERRCODE = 'P0006';
        END IF;
        v_balance_before := v_hard_gems::INT;
        UPDATE user_wallet SET hard_gems = hard_gems - v_price_int WHERE user_id = p_user_id;
        v_balance_after := v_balance_before - v_price_int;
    ELSE
        RAISE EXCEPTION 'InvalidPayment' USING ERRCODE = 'P0003';
    END IF;

    v_transaction_id := gen_random_uuid();
    INSERT INTO transaction (transaction_id, user_id, coin_type, amount, balance_before, balance_after, operation, reason, reference_id)
    VALUES (v_transaction_id, p_user_id, v_coin_type, v_price_int, v_balance_before, v_balance_after, 'DEBIT', 'SHOP_PURCHASE', p_offer_id);
    v_transaction_ids := v_transaction_ids || to_jsonb(v_transaction_id::TEXT);

    -- Process rewards
    SELECT jsonb_agg(jsonb_build_object(
        'reward_type', reward_type,
        'reward_reference', reward_reference,
        'quantity', quantity
    )) INTO v_rewards
    FROM offer_reward WHERE offer_id = p_offer_id;

    IF v_rewards IS NOT NULL THEN
        FOR v_elem IN SELECT * FROM jsonb_array_elements(v_rewards)
        LOOP
            v_reward_type := v_elem->>'reward_type';
            v_reward_ref := CASE WHEN v_elem->>'reward_reference' IS NOT NULL AND v_elem->>'reward_reference' != 'null' THEN (v_elem->>'reward_reference')::UUID ELSE NULL END;
            v_quantity := COALESCE((v_elem->>'quantity')::INT, 1);

            IF v_reward_type = 'COIN' THEN
                SELECT soft_coins INTO v_soft_coins FROM user_wallet WHERE user_id = p_user_id FOR UPDATE;
                v_balance_before := v_soft_coins::INT;
                UPDATE user_wallet SET soft_coins = soft_coins + v_quantity WHERE user_id = p_user_id;
                v_balance_after := v_balance_before + v_quantity;
                v_transaction_id := gen_random_uuid();
                INSERT INTO transaction (transaction_id, user_id, coin_type, amount, balance_before, balance_after, operation, reason, reference_id)
                VALUES (v_transaction_id, p_user_id, 'SOFT', v_quantity, v_balance_before, v_balance_after, 'CREDIT', 'SHOP_PURCHASE', p_offer_id);
                v_transaction_ids := v_transaction_ids || to_jsonb(v_transaction_id::TEXT);
            ELSIF v_reward_type = 'GEMS' THEN
                SELECT hard_gems INTO v_hard_gems FROM user_wallet WHERE user_id = p_user_id FOR UPDATE;
                v_balance_before := v_hard_gems::INT;
                UPDATE user_wallet SET hard_gems = hard_gems + v_quantity WHERE user_id = p_user_id;
                v_balance_after := v_balance_before + v_quantity;
                v_transaction_id := gen_random_uuid();
                INSERT INTO transaction (transaction_id, user_id, coin_type, amount, balance_before, balance_after, operation, reason, reference_id)
                VALUES (v_transaction_id, p_user_id, 'HARD', v_quantity, v_balance_before, v_balance_after, 'CREDIT', 'SHOP_PURCHASE', p_offer_id);
                v_transaction_ids := v_transaction_ids || to_jsonb(v_transaction_id::TEXT);
            ELSIF v_reward_type = 'COSMETIC' THEN
                IF v_reward_ref IS NULL THEN
                    RAISE EXCEPTION 'CosmeticNotFound' USING ERRCODE = 'P0007';
                END IF;
                SELECT EXISTS(SELECT 1 FROM cosmetic WHERE cosmetic_id = v_reward_ref) INTO v_cosmetic_exists;
                IF NOT v_cosmetic_exists THEN
                    RAISE EXCEPTION 'CosmeticNotFound' USING ERRCODE = 'P0007';
                END IF;
                IF EXISTS(SELECT 1 FROM user_inventory WHERE user_id = p_user_id AND cosmetic_id = v_reward_ref) THEN
                    RAISE EXCEPTION 'InvalidCosmetic' USING ERRCODE = 'P0008';
                END IF;
                INSERT INTO user_inventory (user_id, cosmetic_id, equipped, unlocked_at)
                VALUES (p_user_id, v_reward_ref, FALSE, NOW());
            END IF;
        END LOOP;
    END IF;

    SELECT soft_coins, hard_gems INTO v_new_soft, v_new_hard FROM user_wallet WHERE user_id = p_user_id;

    v_result := jsonb_build_object(
        'transaction_ids', v_transaction_ids,
        'new_soft_coins', v_new_soft,
        'new_hard_gems', v_new_hard
    );
    RETURN v_result;
END;
$$;

-- ============================================================================
-- SP-07: Grant / Revoke operations
-- ============================================================================

CREATE OR REPLACE FUNCTION sp_grant_wallet_reward(
    p_user_id UUID,
    p_coin_type VARCHAR(50),
    p_amount INT,
    p_reason VARCHAR(50),
    p_reference_id UUID
)
RETURNS JSONB
LANGUAGE plpgsql AS $$
DECLARE
    v_balance_before INT;
    v_balance_after INT;
    v_transaction_id UUID;
    v_new_soft BIGINT;
    v_new_hard BIGINT;
BEGIN
    IF p_coin_type = 'SOFT' THEN
        SELECT soft_coins INTO v_new_soft FROM user_wallet WHERE user_id = p_user_id FOR UPDATE;
        IF NOT FOUND THEN RAISE EXCEPTION 'UserNotFound' USING ERRCODE = 'P0005'; END IF;
        v_balance_before := v_new_soft::INT;
        UPDATE user_wallet SET soft_coins = soft_coins + p_amount WHERE user_id = p_user_id;
        v_balance_after := v_balance_before + p_amount;
    ELSIF p_coin_type = 'HARD' THEN
        SELECT hard_gems INTO v_new_hard FROM user_wallet WHERE user_id = p_user_id FOR UPDATE;
        IF NOT FOUND THEN RAISE EXCEPTION 'UserNotFound' USING ERRCODE = 'P0005'; END IF;
        v_balance_before := v_new_hard::INT;
        UPDATE user_wallet SET hard_gems = hard_gems + p_amount WHERE user_id = p_user_id;
        v_balance_after := v_balance_before + p_amount;
    ELSE
        RAISE EXCEPTION 'InvalidPayment' USING ERRCODE = 'P0003';
    END IF;

    v_transaction_id := gen_random_uuid();
    INSERT INTO transaction (transaction_id, user_id, coin_type, amount, balance_before, balance_after, operation, reason, reference_id)
    VALUES (v_transaction_id, p_user_id, p_coin_type, p_amount, v_balance_before, v_balance_after, 'CREDIT', p_reason, p_reference_id);

    SELECT soft_coins, hard_gems INTO v_new_soft, v_new_hard FROM user_wallet WHERE user_id = p_user_id;
    RETURN jsonb_build_object('transaction_id', v_transaction_id::TEXT, 'new_soft_coins', v_new_soft, 'new_hard_gems', v_new_hard);
END;
$$;

CREATE OR REPLACE FUNCTION sp_revoke_wallet(
    p_user_id UUID,
    p_coin_type VARCHAR(50),
    p_amount INT,
    p_reference_id UUID
)
RETURNS JSONB
LANGUAGE plpgsql AS $$
DECLARE
    v_balance_before INT;
    v_balance_after INT;
    v_transaction_id UUID;
    v_soft BIGINT;
    v_hard BIGINT;
BEGIN
    IF p_coin_type = 'SOFT' THEN
        SELECT soft_coins INTO v_soft FROM user_wallet WHERE user_id = p_user_id FOR UPDATE;
        IF NOT FOUND THEN RAISE EXCEPTION 'UserNotFound' USING ERRCODE = 'P0005'; END IF;
        IF v_soft < p_amount THEN RAISE EXCEPTION 'InsufficientBalance' USING ERRCODE = 'P0006'; END IF;
        v_balance_before := v_soft::INT;
        UPDATE user_wallet SET soft_coins = soft_coins - p_amount WHERE user_id = p_user_id;
        v_balance_after := v_balance_before - p_amount;
    ELSIF p_coin_type = 'HARD' THEN
        SELECT hard_gems INTO v_hard FROM user_wallet WHERE user_id = p_user_id FOR UPDATE;
        IF NOT FOUND THEN RAISE EXCEPTION 'UserNotFound' USING ERRCODE = 'P0005'; END IF;
        IF v_hard < p_amount THEN RAISE EXCEPTION 'InsufficientBalance' USING ERRCODE = 'P0006'; END IF;
        v_balance_before := v_hard::INT;
        UPDATE user_wallet SET hard_gems = hard_gems - p_amount WHERE user_id = p_user_id;
        v_balance_after := v_balance_before - p_amount;
    ELSE
        RAISE EXCEPTION 'InvalidPayment' USING ERRCODE = 'P0003';
    END IF;

    v_transaction_id := gen_random_uuid();
    INSERT INTO transaction (transaction_id, user_id, coin_type, amount, balance_before, balance_after, operation, reason, reference_id)
    VALUES (v_transaction_id, p_user_id, p_coin_type, p_amount, v_balance_before, v_balance_after, 'DEBIT', 'ADMIN_REVOKE', p_reference_id);

    SELECT soft_coins, hard_gems INTO v_soft, v_hard FROM user_wallet WHERE user_id = p_user_id;
    RETURN jsonb_build_object('transaction_id', v_transaction_id::TEXT, 'new_soft_coins', v_soft, 'new_hard_gems', v_hard);
END;
$$;

CREATE OR REPLACE FUNCTION sp_grant_cosmetic(
    p_user_id UUID,
    p_cosmetic_id UUID
)
RETURNS VOID
LANGUAGE plpgsql AS $$
DECLARE v_exists BOOLEAN;
BEGIN
    SELECT EXISTS(SELECT 1 FROM cosmetic WHERE cosmetic_id = p_cosmetic_id) INTO v_exists;
    IF NOT v_exists THEN RAISE EXCEPTION 'CosmeticNotFound' USING ERRCODE = 'P0007'; END IF;
    IF EXISTS(SELECT 1 FROM user_inventory WHERE user_id = p_user_id AND cosmetic_id = p_cosmetic_id) THEN
        RAISE EXCEPTION 'InvalidCosmetic' USING ERRCODE = 'P0008';
    END IF;
    INSERT INTO user_inventory (user_id, cosmetic_id, equipped, unlocked_at)
    VALUES (p_user_id, p_cosmetic_id, FALSE, NOW());
END;
$$;

CREATE OR REPLACE FUNCTION sp_revoke_cosmetic(
    p_user_id UUID,
    p_cosmetic_id UUID
)
RETURNS JSONB
LANGUAGE plpgsql AS $$
DECLARE
    v_equipped BOOLEAN;
    v_type VARCHAR(50);
    v_fallback UUID;
    v_movements JSONB := '[]'::JSONB;
BEGIN
    SELECT equipped INTO v_equipped FROM user_inventory WHERE user_id = p_user_id AND cosmetic_id = p_cosmetic_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'InvalidUserCosmeticSelected' USING ERRCODE = 'P0009';
    END IF;

    SELECT type INTO v_type FROM cosmetic WHERE cosmetic_id = p_cosmetic_id;

    DELETE FROM user_inventory WHERE user_id = p_user_id AND cosmetic_id = p_cosmetic_id;
    v_movements := v_movements || jsonb_build_object('cosmetic_id', p_cosmetic_id::TEXT, 'change', 'REMOVED', 'was_equipped', v_equipped);

    IF v_equipped THEN
        SELECT ui.cosmetic_id INTO v_fallback
        FROM user_inventory ui
        JOIN cosmetic c ON c.cosmetic_id = ui.cosmetic_id
        WHERE ui.user_id = p_user_id AND c.type = v_type
        LIMIT 1;
        IF v_fallback IS NOT NULL THEN
            UPDATE user_inventory SET equipped = TRUE WHERE user_id = p_user_id AND cosmetic_id = v_fallback;
            v_movements := v_movements || jsonb_build_object('cosmetic_id', v_fallback::TEXT, 'change', 'EQUIPPED', 'was_equipped', FALSE);
        END IF;
    END IF;
    RETURN v_movements;
END;
$$;

-- ============================================================================
-- SP-08: Ban / Unban — atomic
-- ============================================================================

CREATE OR REPLACE PROCEDURE sp_ban_user(
    p_ban_history_id UUID,
    p_user_id UUID,
    p_admin_id UUID,
    p_reason VARCHAR(500),
    p_type VARCHAR(50),
    p_expires_at TIMESTAMP
)
LANGUAGE plpgsql AS $$
BEGIN
    IF EXISTS (SELECT 1 FROM ban_history WHERE user_id = p_user_id AND removed_at IS NULL AND (expires_at IS NULL OR expires_at > NOW())) THEN
        RAISE EXCEPTION 'UserAlreadyWasBanned' USING ERRCODE = 'P0010';
    END IF;
    INSERT INTO ban_history (ban_history_id, user_id, admin_id, reason, type, expires_at, created_at)
    VALUES (p_ban_history_id, p_user_id, p_admin_id, p_reason, p_type, p_expires_at, NOW());
END;
$$;

CREATE OR REPLACE PROCEDURE sp_unban_user(
    p_user_id UUID,
    p_admin_id UUID
)
LANGUAGE plpgsql AS $$
DECLARE v_ban_id UUID;
BEGIN
    SELECT ban_history_id INTO v_ban_id FROM ban_history
    WHERE user_id = p_user_id AND removed_at IS NULL AND (expires_at IS NULL OR expires_at > NOW())
    FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'UserDoesNotHaveBan' USING ERRCODE = 'P0011';
    END IF;
    UPDATE ban_history SET removed_at = NOW(), removed_by = p_admin_id WHERE ban_history_id = v_ban_id;
END;
$$;

-- ============================================================================
-- SP-10: Nickname and token — eliminate TOCTOU
-- ============================================================================

CREATE OR REPLACE PROCEDURE sp_change_nickname(
    p_user_id UUID,
    p_new_username VARCHAR(15)
)
LANGUAGE plpgsql AS $$
DECLARE v_can_change BOOLEAN;
BEGIN
    SELECT can_change_nickname INTO v_can_change FROM "user" WHERE user_id = p_user_id FOR UPDATE;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'UserNotFound' USING ERRCODE = 'P0005';
    END IF;
    IF NOT v_can_change THEN
        RAISE EXCEPTION 'UserCannotChangeNickname' USING ERRCODE = 'P0012';
    END IF;
    IF EXISTS (SELECT 1 FROM "user" WHERE username = p_new_username) THEN
        RAISE EXCEPTION 'NicknameAlreadyInUse' USING ERRCODE = 'P0013';
    END IF;
    UPDATE "user" SET username = p_new_username, can_change_nickname = FALSE WHERE user_id = p_user_id;
EXCEPTION WHEN unique_violation THEN
    RAISE EXCEPTION 'NicknameAlreadyInUse' USING ERRCODE = 'P0013';
END;
$$;

CREATE OR REPLACE PROCEDURE sp_refresh_token(
    p_user_id UUID,
    p_token_version UUID
)
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE "user" SET token_version = p_token_version WHERE user_id = p_user_id;
    IF NOT FOUND THEN
        RAISE EXCEPTION 'UserNotFound' USING ERRCODE = 'P0005';
    END IF;
END;
$$;

-- ============================================================================
-- Admin save with permissions — atomic
-- ============================================================================

CREATE OR REPLACE PROCEDURE sp_admin_save(
    p_admin_id UUID,
    p_name VARCHAR(50),
    p_email VARCHAR(50),
    p_password_hash VARCHAR(100),
    p_token_version UUID,
    p_is_super BOOLEAN,
    p_permissions JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    INSERT INTO admin (admin_id, name, email, password_hash, token_version, is_super, created_at)
    VALUES (p_admin_id, p_name, p_email, p_password_hash, p_token_version, p_is_super, NOW())
    ON CONFLICT (admin_id) DO UPDATE SET
        name = EXCLUDED.name,
        email = EXCLUDED.email,
        password_hash = EXCLUDED.password_hash,
        token_version = EXCLUDED.token_version,
        is_super = EXCLUDED.is_super;

    DELETE FROM admin_permission WHERE admin_id = p_admin_id;

    IF p_permissions IS NOT NULL AND jsonb_array_length(p_permissions) > 0 THEN
        INSERT INTO admin_permission (admin_id, permission_key, action)
        SELECT
            p_admin_id,
            elem->>'permission_key',
            elem->>'action'
        FROM jsonb_array_elements(p_permissions) AS elem;
    END IF;
EXCEPTION WHEN unique_violation THEN
    RAISE EXCEPTION 'EmailAlreadyInUse' USING ERRCODE = 'P0014';
END;
$$;

-- Helper for admin reads with permissions aggregated
CREATE OR REPLACE FUNCTION sp_admin_find_by_id(p_admin_id UUID)
RETURNS TABLE(
    admin_id UUID,
    name VARCHAR(50),
    email VARCHAR(50),
    password_hash VARCHAR(100),
    token_version UUID,
    is_super BOOLEAN,
    created_at TIMESTAMPTZ,
    permissions JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        a.admin_id,
        a.name,
        a.email,
        a.password_hash,
        a.token_version,
        a.is_super,
        a.created_at,
        COALESCE(
            (SELECT jsonb_agg(jsonb_build_object('permission_key', ap.permission_key, 'action', ap.action))
             FROM admin_permission ap WHERE ap.admin_id = a.admin_id),
            '[]'::JSONB
        ) AS permissions
    FROM admin a WHERE a.admin_id = p_admin_id;
END;
$$;

CREATE OR REPLACE FUNCTION sp_admin_find_by_email(p_email VARCHAR)
RETURNS TABLE(
    admin_id UUID,
    name VARCHAR(50),
    email VARCHAR(50),
    password_hash VARCHAR(100),
    token_version UUID,
    is_super BOOLEAN,
    created_at TIMESTAMPTZ,
    permissions JSONB
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        a.admin_id,
        a.name,
        a.email,
        a.password_hash,
        a.token_version,
        a.is_super,
        a.created_at,
        COALESCE(
            (SELECT jsonb_agg(jsonb_build_object('permission_key', ap.permission_key, 'action', ap.action))
             FROM admin_permission ap WHERE ap.admin_id = a.admin_id),
            '[]'::JSONB
        ) AS permissions
    FROM admin a WHERE a.email = p_email;
END;
$$;

-- ============================================================================
-- Game read helper — optional but for completeness
-- ============================================================================

CREATE OR REPLACE FUNCTION sp_admin_find_page(p_limit INT, p_offset INT)
RETURNS TABLE(
    admin_id UUID,
    name VARCHAR(50),
    email VARCHAR(50),
    password_hash VARCHAR(100),
    token_version UUID,
    is_super BOOLEAN,
    created_at TIMESTAMPTZ,
    permissions JSONB,
    total_count BIGINT
)
LANGUAGE plpgsql AS $$
BEGIN
    RETURN QUERY
    SELECT
        a.admin_id,
        a.name,
        a.email,
        a.password_hash,
        a.token_version,
        a.is_super,
        a.created_at,
        COALESCE(
            (SELECT jsonb_agg(jsonb_build_object('permission_key', ap.permission_key, 'action', ap.action))
             FROM admin_permission ap WHERE ap.admin_id = a.admin_id),
            '[]'::JSONB
        ) AS permissions,
        COUNT(*) OVER() AS total_count
    FROM admin a
    ORDER BY a.created_at ASC
    LIMIT p_limit OFFSET p_offset;
END;
$$;

