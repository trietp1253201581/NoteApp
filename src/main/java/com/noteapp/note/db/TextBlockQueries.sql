--GET_ALL_BY_BLOCK
SELECT block_id, editor, content
FROM text_blocks
WHERE block_id = ?;

--CREATE
INSERT INTO text_blocks(block_id, editor, content)
VALUES (?, ?, ?);

--UPDATE
UPDATE text_blocks SET content = ?
WHERE block_id = ? AND editor = ?;

--END