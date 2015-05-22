INSERT 
INTO 
users
                (id,    name,          password,       bdate) 
VALUES 
                (1,     'Intey',       '123',          '06-10-1990'),
                (2,     'andreyk',     '123',          '10-10-1989');

INSERT 
INTO 
events
                (id,    name,          price,          remain) 
VALUES
                (1,     'Cookies',     '124',          '124'),
                (2,     'Tea',         '50',           '50'),
                (3,     'Pizza',       '1300',         '1300');

-- Cookies - Intey, AndreyK; Tea - AndreyK; Pizza - None;
INSERT 
INTO 
participants
                (eid,           uid) 
VALUES
                (1,             1),
                (2,             1),
                (2,             2);
                
