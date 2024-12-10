--GET_ALL_BY_NOTE
SELECT filter FROM note_filters 
WHERE note_id = ?
ORDER BY filter;

--CREATE
INSERT INTO note_filters(note_id, filter)
VALUES (?, ?);

--DELETE_ALL
DELETE FROM note_filters 
WHERE note_id = ?;

--END