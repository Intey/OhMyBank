CREATE TABLE users(
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] varchar(50) NOT NULL,
    [bdate] datetime NOT NULL,
    [balance] INTEGER NOT NULL DEFAULT 0);
