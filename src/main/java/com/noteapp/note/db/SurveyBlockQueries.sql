--GET_ALL_BY_BLOCK
SELECT block_id, editor, survey_map
FROM survey_blocks
WHERE block_id = ?;

--CREATE
INSERT INTO survey_blocks(block_id, editor, survey_map)
VALUES(?, ?, ?);

--UPDATE
UPDATE survey_blocks SET survey_map = ?
WHERE block_id = ? AND editor = ?;

--END