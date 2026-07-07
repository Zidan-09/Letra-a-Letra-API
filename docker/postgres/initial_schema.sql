CREATE TABLE "user" (
                        "user_id" uuid PRIMARY KEY NOT NULL,
                        "username" varchar(15) UNIQUE NOT NULL,
                        "email" varchar(50) UNIQUE NOT NULL,
                        "password_hash" varchar(100),
                        "google_id" varchar(100) UNIQUE,
                        "can_change_nickname" boolean DEFAULT TRUE,
                        "created_at" timestamptz DEFAULT CURRENT_TIMESTAMP,
                        CONSTRAINT check_auth_method
                            CHECK (password_hash IS NOT NULL OR google_id IS NOT NULL)
);

CREATE TABLE "user_stats" (
                        "user_id" uuid PRIMARY KEY REFERENCES "user" ("user_id") ON DELETE CASCADE,
                        "total_matches" integer DEFAULT 0,
                        "total_wins" integer DEFAULT 0,
                        "win_streak" integer DEFAULT 0,
                        "level" integer DEFAULT 1,
                        "experience" integer DEFAULT 0,
                        ranking_points integer DEFAULT 0
);

CREATE TABLE "user_wallet" (
                        "user_id" uuid PRIMARY KEY REFERENCES "user" ("user_id") ON DELETE CASCADE,
                        "soft_coins" bigint NOT NULL DEFAULT 0 CHECK ("soft_coins" >= 0),
                        "hard_gems" bigint NOT NULL DEFAULT 0 CHECK ("hard_gems" >= 0)
);

CREATE TABLE "game" (
                        "game_id" uuid PRIMARY KEY NOT NULL,
                        "host_id" uuid REFERENCES "user" ("user_id"),
                        "created_by_id" uuid REFERENCES "user" ("user_id"),
                        "room_code" varchar(50) NOT NULL,
                        "game_type" varchar(50) NOT NULL,
                        "created_at" timestamp DEFAULT CURRENT_TIMESTAMP,
                        "status" varchar(50)
);

CREATE TABLE "matches" (
                        "match_id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                        "game_id" uuid REFERENCES "game" ("game_id") ON DELETE CASCADE,
                        "game_mode" varchar(50) NOT NULL,
                        "started_at" timestamp DEFAULT CURRENT_TIMESTAMP,
                        "ended_at" timestamp
);

CREATE TABLE "match_players" (
                        "match_id" uuid REFERENCES "matches" ("match_id") ON DELETE CASCADE,
                        "user_id" uuid REFERENCES "user" ("user_id") ON DELETE CASCADE,
                        "score" integer DEFAULT 0,
                        "is_winner" boolean DEFAULT false,
                        PRIMARY KEY ("match_id", "user_id")
);

CREATE TABLE "cosmetic" (
                        "cosmetic_id" uuid PRIMARY KEY NOT NULL,
                        "name" varchar(50) UNIQUE NOT NULL,
                        "type" varchar(50) NOT NULL,
                        "asset_path" varchar(50) NOT NULL,
                        "version" integer NOT NULL DEFAULT 1,
                        "available" boolean DEFAULT true
);

CREATE TABLE "user_inventory" (
                        "user_id" uuid REFERENCES "user" ("user_id") ON DELETE CASCADE,
                        "cosmetic_id" uuid REFERENCES "cosmetic" ("cosmetic_id") ON DELETE CASCADE,
                        "equipped" boolean DEFAULT false,
                        "unlocked_at" timestamptz DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY ("user_id", "cosmetic_id")
);

CREATE TABLE "store_offer" (
                       "offer_id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                       "title" varchar(100) NOT NULL,
                       "coin_type" varchar(50) NOT NULL,
                       "price" integer NOT NULL CHECK ("price" > 0),
                       "target_cosmetic_id" uuid REFERENCES "cosmetic" ("cosmetic_id") ON DELETE SET NULL,
                       "reward_soft_coins" integer DEFAULT 0,
                       "reward_hard_gems" integer DEFAULT 0,
                       "active" boolean NOT NULL DEFAULT true,
                       "expires_at" timestamptz
);

CREATE TABLE "wallet_log" (
                      "log_id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                      "user_id" uuid NOT NULL REFERENCES "user" ("user_id") ON DELETE CASCADE,
                      "coin_type" varchar(50) NOT NULL,
                      "amount" integer NOT NULL,
                      "balance_after" integer NOT NULL,
                      "reason" varchar(50) NOT NULL,
                      "created_at" timestamptz DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE "friend" (
                      "user_id_1" uuid,
                      "user_id_2" uuid,
                      "status" varchar(50) NOT NULL,
                      "request_date" timestamp DEFAULT CURRENT_TIMESTAMP,
                      PRIMARY KEY ("user_id_1", "user_id_2")
);

CREATE TABLE "admin" (
                    "admin_id" uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                    "name" varchar(50) NOT NULL,
                    "email" varchar(50) UNIQUE NOT NULL,
                    "password_hash" varchar(100) NOT NULL,
                    "created_at" timestamptz DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_game_room_code_active ON "game" ("room_code") WHERE status = 'WAITING';
CREATE INDEX idx_user_stats_wins ON "user_stats" ("total_wins" DESC);
CREATE INDEX idx_match_players_user ON "match_players" ("user_id");