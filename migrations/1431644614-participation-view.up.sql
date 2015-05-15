CREATE VIEW participation
AS 
SELECT e.name event, e.date, e.price, e.remain, u.name username
FROM events e
LEFT JOIN participants p
on e.id = p.eid
LEFT JOIN users u
on u.id = p.uid;
