
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS elo INT DEFAULT 1200,
    ADD COLUMN IF NOT EXISTS account_created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

CREATE TABLE IF NOT EXISTS matches (
    id SERIAL PRIMARY KEY,
    status VARCHAR(20) DEFAULT 'PENDING',
    started_at TIMESTAMP,
    ended_at TIMESTAMP,
    );

CREATE TABLE IF NOT EXISTS user_matches (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    match_id INT NOT NULL REFERENCES matches(id) ON DELETE CASCADE,
    color VARCHAR(5) CHECK (color IN ('WHITE', 'BLACK')),
    moves TEXT,
    result VARCHAR(10),
    elo_change INT DEFAULT 0,
    UNIQUE (user_id, match_id)
    );
