-- V3__init_add_moves_matches

ALTER TABLE matches
    ADD COLUMN IF NOT EXISTS moves TEXT, ADD COLUMN IF NOT EXISTS turn TEXT;


UPDATE matches m
SET moves = um.moves
    FROM (
    SELECT DISTINCT ON (match_id) match_id, moves
    FROM user_matches
    WHERE moves IS NOT NULL
    ORDER BY match_id, id   -- take the earliest user_match row
) AS um
WHERE m.id = um.match_id;


-- WITH unique_moves AS (
--     SELECT DISTINCT match_id, moves
--     FROM user_matches
--     WHERE moves IS NOT NULL
-- )
-- UPDATE matches m
-- SET moves = u.moves
--     FROM unique_moves u
-- WHERE m.id = u.match_id;