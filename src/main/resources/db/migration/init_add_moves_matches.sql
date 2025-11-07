
-- V3__init_add_moves_matches

ALTER TABLE matches ADD COLUMN IF NOT EXISTS moves TEXT;


-- TODO copy moves from user_matches