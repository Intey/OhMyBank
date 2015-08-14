INSERT 
INTO 
users
                (id,    name,          password,       bdate) 
VALUES 
                (1,     'Intey',       '123',          '06-10-1990'),
                (2,     'andreyk',     '123',          '10-10-1989')
                ;

INSERT 
INTO 
events
                (id,    name,          price,          author,      status) 
VALUES
                (1,     'Cookies',     '124',          'Intey',     "initial"),
                (2,     'Tea',         '50',           'Intey',     "initial"),
                (3,     'Pizza',       '1300',         'andreyk',   "initial")
                ;

INSERT 
INTO 
participation
                (uid,   eid)
VALUES
                (1,     1),
                (1,     2),
                (2,     1),
                (2,     3);
