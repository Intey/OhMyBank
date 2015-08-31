INSERT INTO users
                (id,    name,          password,       bdate) 
VALUES 
                (1,     'Intey',       '123',          '06-10-1990'),
                (2,     'andreyk',     '123',          '10-10-1989')
                ;

INSERT INTO events
                (id,    name,          price,          author,      status,     parts) 
VALUES
                (1,     'Cookies',     '124',          'Intey',     "initial",  1),
                (2,     'Tea',         '50',           'andreyk',   "initial",  1),
                (3,     'Pizza',       '1300',         'Intey',     "initial",  8)
                ;

INSERT INTO goods
                (eid,   rest)
VALUES
                (3,     8);
INSERT INTO participation
                (uid,   eid)
VALUES
                (1,     1),
                (1,     3),
                (2,     2),
                (2,     3);
