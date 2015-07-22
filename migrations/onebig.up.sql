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
SELECT e.name event, e.date, e.price, u.name user, sum(debit) debits, sum(credit) credits, 
(sum(credit) - credit) should_be_zero -- Just a hypothesis: same event with same user have multiple credits, thats strange. 
FROM pays p 
INNER JOIN users u
ON p.uid = u.id
INNER JOIN events e
ON p.eid = e.id
group by eid; -- for hypothesis 

