(ns ombs.handler.birthday
  (:require
    [ombs.db.old :as db]
    [ombs.db.payment :as dbpay]
    [ombs.validate :as isvalid]
    [ombs.funcs :as funcs]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [ombs.view.pages :refer [addevent] ]
    )
  )

(defn birthday-party-pay [rates userrate eventprice]
  "calculate party-pay for birthday. Use rates"
  (* (/ eventprice (reduce + rates)) userrate)
  )

(defn add-birthday [ {event :name price :price date :date users :participants parts :parts :as params} ]
  "Add event in events table, with adding participants, and calculating debts."
  ;valudation
  (if (isvalid/new-event? event price date users)
    (let [user-rates (db/get-rates (funcs/as-vec users))
          party-pay (/ (read-string price) (reduce + user-rates)) ]
      (db/add-event event (read-string price) (sess/get :username) date parts)
      ;use 'dorun' for execute lazy function 'db/credit-payment'
      (dorun
        (map
          #(dbpay/credit-payment (db/get-eid event date) (db/get-uid %) (* party-pay (db/get-rate %)))
          (funcs/as-vec users)))
      (redirect "/user"))
    (addevent)

    )
  )
