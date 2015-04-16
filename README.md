# ombs
Little bank project for my friends. 
At work (yes, we work) we often buy cookies and tea, make gifts at birthday,
and so on. Ad-hoc we don't have money for pay, so we remember a debt. But, when
we have many events, debt can be lost. At first time, we usee excel. Not bad,
but so little automatiozation and human factor:
* One pay with creadit card, one with hand-cash
* Bankir needs write debts. But also he needs work! Party time in his brain!
* Some else

## And there ombs come Oh my Bank (Small)
It's a web-app made on clojure libs [ring](...), [compojure](...) and many 
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

##calculations 
User debt - when user sets to participation in some event, his balance 
decreased on party-pay

### Example
Tea {participants: 5, price: 70} 
* all users have rate = 1 
    So, each user party-pay = (price / participants)
* all users have different rate
    user party-pay = (price / (one_price)*participats - rate)
    
