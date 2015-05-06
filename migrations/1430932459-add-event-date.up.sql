DROP TABLE events;
CREATE TABLE events(
    [id] INTEGER PRIMARY KEY AUTOINCREMENT,
    [name] VARCHAR(200) NOT NULL,
    [price] INTEGER NOT NULL,
    [remain] INTEGER NOT NULL,
    [date] DATE NOT NULL DEFAULT current_timestamp);

