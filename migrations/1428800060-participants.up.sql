CREATE TABLE participants(
    [eid] INTEGER REFERENCES events(id),
    [uid] INTEGER REFERENCES users(id));
