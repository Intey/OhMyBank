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
pays
                (eid,   uid,    debit,  credit) 
VALUES
                (1,     1,      0,      124),   -- participate
                (2,     1,      0,      25),    -- participate
                (2,     2,      0,      25),    -- participate
                (1,     1,      100,    0),     -- pay
                (1,     1,      24,     0),     -- pay
                (2,     2,      25,     0)      -- pay
                ;
                
