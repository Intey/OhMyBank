

# ombs
Little bank project for my friends.
At work (yes, we work) we often buy cookies and tea, make gifts at birthday,
and so on. Ad-hoc we don't have money for pay, so we remember a debt. But, when
we have many events, debt can be lost. At first time, we usee excel. Not bad,
but so little automatiozation and human factor:
- One pay with creadit card, one with hand-cash
- Bankir needs write debts. But also he needs work! Party time in his brain!
- Some else

## And there ombs come Oh my Bank (Small)
It's a web-app made on clojure libs [ring](https://github.com/ring-clojure/ring), [compojure](https://github.com/weavejester/compojure) and many
other.
Front side provide common functions:
- add user
    name, birthday, rate (default 1 - full),
    balance (debt if less than 0)
- add event
    name, date, price, remaining
- dispay user debts
    Show events with debts for user
- pay debt (full=true)
- auto-create birthday events
- auto close event
    when all users full pay(by parts or in one time)

## TODO
- [x] rate for event (NOTE: pizza parts)
- [x] money output
- [ ] holiday auto event
- [ ] fix bugs

## Commands
`lein test` for run all specs.
`lein spec -a` for run watcher, that run tests after their update.
`lein ancient upgrade` for upgrade all dependencies. Be *on the ledge*!
`lein repl` for run REPL with this project (My advice is use [LightTable](http://lighttable.com)).
`lein ring server` start server. Also open browser on index page.
`lein ring server-headless` start server. without browser.

## Flow
req
+++
┃ layer          | in                     | description
┃ handler layer  | json parsed primitives | prepare income params for domain layer (expand params to recors). | no
┃                |                        | flow branch param: ???
┃ validate layer | records ^ primitives   | validate input from handler layer.                                | yes
┃ domain layer   | records ^ primitives   | execute domain funcs. grouped db funcs, in data morfing.          | no
∨ db layer       | records ^ primitives   | just sql's.                                                       |
+++
res

## Records(Models)
### User
    + name
    + dbate(birth date)
    + password
    + avatar
    + rate:
        handicap. By default - 1.0; For students - 0.5.  If, in event
        participate 2 users, one with 1.0 and second with 0.5, so first should
        pay 2/3, and student - 1/3. 50% discount, yo.
    + role:
        admin, banker, client

### Event
    + name
    + date
    + price
    + author
    + date
    + status

### Transaction
Show user payment actions on events.

### Fee
Show user intention to pay in some event. Should be described by admin.

### Error
I going to hold validations and predicates in VALIDATION Layer. If they fall,
Error generated and returned to client.
    - id
    - description
    - where (where it's appears)

## Functions

### HANDLER Layer

There, as sayed above, each action can return ERROR record.

| name             | in                    | out         |
| ---------------  | ----------------      | ----------- |
| affirm           | fee ^ fid             | nil(ok)     |
| refute           | fee ^ fid             | nil(ok)     |
| moneyout         | fee ^ fid             | nil(ok)     |
| participate      | EVENT ^ USER          | nil(ok)     |
| pay              | EVENT ^ USER          | nil(ok)     |
| log-in           | USER                  | TOKEN?      |
| log-out          | USER                  | nil(ok)     |
| register         | USER                  | nil(ok)     |
| add-event        | EVENT ^ EVENT, [USER] | id          |
| add-participants | EVENT, [USER]         | nil(ok)     |
| add-user         | USER                  | id          |
| get-event        | eid                   | EVENT       |
| get-events       | nil ^ FILTERS         | [EVENT]     |

### DB Layer

| name               | in                           | out              |
|--------------------|------------------------------|------------------|
| party-pay          | EVENT, [USER] ^ price, USER  | integer          |
| credit-payment     | EVENT, USER ^ eid, uid       | transaction-id   |
| debit-payment      | EVENT, USER ^ eid, uid       | transaction-id   |
| get-debt           | EVENT, USER ^ eid, uid       | integer          |
| participated?      | EVENT, USER ^ eid, uid       | bool ^ ERROR     |
| set-status         | EVENT ^ eid                  | eid ^ ERROR      |
| get-rate           | USER ^ uid                   | integer ^ ERROR  |
| get-rates          | [USER] ^ [uid]               | [integer]        |

### design links
- [Simple Grid](http://tympanus.net/codrops/2013/04/17/responsive-full-width-grid)
- [Improved Grid](http://tympanus.net/Development/AnimatedGridLayout)
- [Product Comparison(Just Nice solution)](http://tympanus.net/codrops/2015/05/26/product-comparison-layout-effect/)

