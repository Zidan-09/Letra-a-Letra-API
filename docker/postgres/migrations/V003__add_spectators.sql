-- V003__add_spectators.sql
-- Adds match_spectators and extends sp_game_save to persist spectators.

CREATE TABLE IF NOT EXISTS "match_spectators" (
    "match_id" UUID NOT NULL REFERENCES "matches" ("match_id") ON DELETE CASCADE,
    "user_id" UUID NOT NULL REFERENCES "user" ("user_id") ON DELETE CASCADE,
    "nickname" VARCHAR(15) NOT NULL,
    PRIMARY KEY ("match_id", "user_id")
);

CREATE INDEX IF NOT EXISTS idx_match_spectators_user ON "match_spectators" ("user_id");
CREATE INDEX IF NOT EXISTS idx_match_spectators_match ON "match_spectators" ("match_id");

-- Drop old signature so we can replace with expanded one (Postgres does not auto-drop on arg count change)
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
