--GET_ALL
SELECT * 
FROM users 
ORDER BY username, name, birthday, school, gender, email;

--GET
SELECT * 
FROM users
WHERE username = ?;

--CREATE
INSERT INTO users(name, username, password, birthday, school, gender, email)
VALUES(?, ?, ?, ?, ?, ?, ?);

--UPDATE
UPDATE users SET name = ?, password = ?, birthday = ?, school = ?, gender = ?, email = ?
WHERE username = ?;

--DELETE
DELETE FROM users 
WHERE username = ?;

--END
