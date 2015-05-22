CREATE TABLE events(
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] VARCHAR(200) NOT NULL,
    [price] INTEGER NOT NULL,
    [remain] INTEGER NOT NULL,
    [date] DATE NOT NULL DEFAULT (date('now')),
    UNIQUE([name], [date])
    );

CREATE TABLE users(
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] varchar(50) NOT NULL UNIQUE,
    [password] varchar(40) NOT NULL,
    [bdate] DATE NOT NULL,
    [rate] DOUBLE NOT NULL DEFAULT 1,
    [balance] INTEGER NOT NULL DEFAULT 0);

CREATE TABLE participants(
    [eid]  INTEGER REFERENCES events(id),
    [uid]  INTEGER REFERENCES users(id),
    [debt] INTEGER NOT NULL DEFAULT 0);

CREATE VIEW participation
AS 
SELECT e.name event, e.date, e.price, e.remain, u.name username
FROM events e
LEFT JOIN participants p
on e.id = p.eid
LEFT JOIN users u
on u.id = p.uid;
