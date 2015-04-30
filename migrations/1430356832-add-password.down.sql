DROP TABLE users;
CREATE TABLE users(
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] varchar(50) NOT NULL UNIQUE,
    [bdate] datetime NOT NULL,
    [rate] DOUBLE NOT NULL DEFAULT 1,
    [balance] INTEGER NOT NULL DEFAULT 0);
