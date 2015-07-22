CREATE TABLE events(
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] VARCHAR(200) NOT NULL,
    [price] DOUBLE NOT NULL,
    [remain] DOUBLE NOT NULL,
    [date] DATE NOT NULL DEFAULT (date('now')),
    UNIQUE([name], [date])
    );

CREATE TABLE users(
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] varchar(50) NOT NULL UNIQUE,
    [password] varchar(40) NOT NULL,
    [bdate] DATE NOT NULL,
    [rate] DOUBLE NOT NULL DEFAULT 1,
    [balance] DOUBLE NOT NULL DEFAULT 0);

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

CREATE VIEW summary
AS 
SELECT e.name event, e.date, e.price, u.name user, sum(debit) debits, sum(credit) credits
FROM events e
LEFT JOIN pays p 
ON e.id = p.eid
LEFT JOIN users u
ON u.id = p.uid
group by p.eid, p.uid;

CREATE VIEW stakes
AS 
SELECT e.name event, e.date, e.price, u.name user
FROM events e
LEFT JOIN pays p
ON e.id = p.eid
LEFT JOIN users u
ON u.id = p.uid
GROUP BY e.name, e.date, u.name;

CREATE VIEW debts
AS 
SELECT user, event, [date], credits - debits debt
FROM summary;
