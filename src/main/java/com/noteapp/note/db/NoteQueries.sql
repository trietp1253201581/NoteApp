--GET_ALL_BY_AUTHOR
SELECT *
FROM notes
WHERE author = ?
ORDER BY last_modified_date, header;

--GET
SELECT *
FROM notes
WHERE note_id = ?;

--CREATE
INSERT INTO notes(author, header, last_modified_date, is_public)
VALUES (?, ?, ?, ?);

--UPDATE
UPDATE notes SET author = ?, header = ?, last_modified_date = ?, is_public = ?
WHERE note_id = ?;

--DELETE
DELETE FROM notes
WHERE note_id = ?;

--END