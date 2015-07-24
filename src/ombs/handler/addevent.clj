(ns ombs.handler.addevent
  (:require 
    [ombs.db :as db]
    [ombs.view.event :as ve]
    [ombs.core :as core]
    [ombs.validate :as isvalid]
    [noir.response :refer [redirect] ]
    ))

(defn addevent-page [& [params]]
    (ve/addevent-page (db/get-usernames) )
  )

(defn party-pay [participants-count eventprice]
  "Return party pay for regular event, not use rates."
  (/ eventprice participants-count)
  )

(defn birthday-party-pay [rates userrate eventprice]
  "calculate party-pay for birthday. Use rates"
  (* (/ eventprice (reduce + rates)) userrate)
  )

;FIXME:
; issue, when we check one user as participant, so there, users - is value(string). When >1, users - vector.
; Solutions:
; * Form should return always vector
; * convert value to vector
(defn addevent [ {event :name price :price date :date users :participants :as params} ]
  "Add event in events table, with adding participants, and calculating debts."
  ;valudation
  (if (isvalid/new-event? event price date users) 
    (let [user-rates (db/get-rates (core/as-vec users))
          party-pay (/ (read-string price) (count users)) ]
      (db/add-event event (read-string price) date)
      ;use 'dorun' for execute lazy function 'db/credit-payment'
      (dorun 
        (map 
          #(db/credit-payment (db/get-uid %) (db/get-eid event date) party-pay)
          (core/as-vec users)))
      (println (str "rates " user-rates " pp " party-pay " users " users))
      (redirect "/user"))
    (addevent-page)
       
    )
  )

(defn add-birthday [ {event :name price :price date :date users :participants :as params} ]
  "Add event in events table, with adding participants, and calculating debts."
  ;valudation
  (if (isvalid/new-event? event price date users) 
    (let [user-rates (db/get-rates (core/as-vec users))
          party-pay (/ (read-string price) (reduce + user-rates)) ]
      (db/add-event event (read-string price) date)
      ;use 'dorun' for execute lazy function 'db/credit-payment'
      (dorun 
        (map 
          #(db/credit-payment (db/get-uid %) (db/get-eid event date) (* party-pay (db/get-rate %))) 
          (core/as-vec users)))
      (println (str "rates " user-rates " pp " party-pay " users " users))
      (redirect "/user"))
    (addevent-page)
       
    )
  )
