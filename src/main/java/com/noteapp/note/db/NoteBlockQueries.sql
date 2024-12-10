--GET_ALL_BY_NOTE
SELECT *
FROM note_blocks
WHERE note_id = ?
ORDER BY block_order;

--CREATE
INSERT INTO note_blocks(note_id, header, block_type, block_order)
VALUES (?, ?, ?, ?);

--UPDATE
UPDATE note_blocks SET note_id = ?, header = ?, block_type = ?, block_order = ?
WHERE block_id = ?;

--DELETE
DELETE FROM note_blocks
WHERE block_id = ?;

--END