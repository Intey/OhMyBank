
CREATE TABLE events(
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] VARCHAR(200) NOT NULL,
    [price] DOUBLE NOT NULL,
    [author] VARCHAR(50) NOT NULL,
    [date] DATE NOT NULL DEFAULT (date('now')),
    [status] VARCHAR(10) NOT NULL,
    [parts] int NOT NULL DEFAULT 1,
    UNIQUE([name], [date])
    );

CREATE TABLE users(
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] varchar(50) NOT NULL UNIQUE,
    [password] varchar(40) NOT NULL,
    [bdate] DATE NOT NULL,
    [rate] DOUBLE NOT NULL DEFAULT 1,
    [balance] DOUBLE NOT NULL DEFAULT 0,
    -- 1 is default user, 0 is admin 
    [role] INTEGER NOT NULL DEFAULT 1);

CREATE TABLE pays(
    [users_id] INTEGER REFERENCES users(id),
    [events_id] INTEGER REFERENCES events(id),
    [debit] DOUBLE NOT NULL DEFAULT 0,
    [credit] DOUBLE NOT NULL DEFAULT 0
);

-- table for unconfirmed payments. They shown for admin, and he can affirm or refute
CREATE TABLE fees(
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [users_id] INTEGER REFERENCES users(id),
    [events_id] INTEGER REFERENCES events(id),
    [date] DATE NOT NULL DEFAULT (date('now')),
    [money] DOUBLE NOT NULL DEFAULT 0,
    [parts] INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE participation(
    [users_id] INTEGER REFERENCES users(id),
    [events_id] INTEGER REFERENCES events(id),
    UNIQUE([users_id], [events_id])
);

CREATE TABLE goods(
    [events_id] INTEGER REFERENCES events(id),
    [rest] INTEGER NOT NULL
);

CREATE VIEW participants
    AS
    SELECT u.name user, e.name event, e.date [date]
    FROM participation p
    LEFT JOIN events e
    ON e.id = p.events_id
    LEFT JOIN users u
    ON u.id = p.users_id;


CREATE VIEW summary
    AS 
    SELECT e.name event, e.date, e.price, u.name user, sum(debit) debits, sum(credit) credits
    FROM events e
    JOIN pays p 
    ON e.id = p.events_id
    LEFT JOIN users u
    ON u.id = p.users_id
    group by p.events_id, p.users_id;

CREATE VIEW debts
    AS 
    SELECT user, event, [date], credits - debits debt
    FROM summary;
