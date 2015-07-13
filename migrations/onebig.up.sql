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

CREATE TABLE pays(
    [uid] INTEGER REFERENCES users(id),
    [eid] INTEGER REFERENCES events(id),
    [debit] DOUBLE NOT NULL DEFAULT 0,
    [credit] DOUBLE NOT NULL DEFAULT 0
);

CREATE TABLE transfers(
    [debiter] INTEGER REFERENCES users(id),
    [crediter] INTEGER REFERENCES users(id),
    [debit] DOUBLE NOT NULL DEFAULT 0,
    [credit] DOUBLE NOT NULL DEFAULT 0,
    UNIQUE(debiter, crediter)
);

CREATE VIEW participation
AS 
SELECT e.name event, e.date, e.price, e.remain, u.name username
FROM events e
LEFT JOIN participants p
on e.id = p.eid
LEFT JOIN users u
on u.id = p.uid;

CREATE VIEW groupedParticipants
AS 
SELECT e.name event, e.date, e.price, e.remain, group_concat(u.name) users
FROM events e
LEFT JOIN participants p
on e.id = p.eid
LEFT JOIN users u
on u.id = p.uid
group by e.name;
