INSERT INTO users
                (id,    name,          password,       bdate,           role)
VALUES
                (1,     'Intey',       '123',          '06-10-1990',    1),
                (2,     'andreyk',     '123',          '10-10-1989',    0)
                ;

INSERT INTO events
                (id,    name,          price,          author,      status,     parts)
VALUES
                (1,     'Cookies',     '124',          'Intey',     "initial",  0),
                (2,     'Tea',         '50',           'andreyk',   "initial",  0),
                (3,     'Pizza',       '1300',         'Intey',     "initial",  8)
                ;

INSERT INTO goods
                (events_id,   rest)
VALUES
                (3,     8);
/* INSERT INTO participation
                (users_id,   events_id)
VALUES
                (1,     1),
                (1,     3),
                (2,     2),
                (2,     3); */
