
(ns ombs.handler.addevent
  (:require 
    [ombs.handler :as handle]
    [ombs.db :as db]
    [ombs.view.common :as vc]
    [noir.validation :as vld]
    [noir.response :refer [redirect] ]
    ))

(defn party-pay [participants-count eventprice]
  "Return party pay for regular event, not use rates."
  (/ eventprice participants-count)
  )

(defn birthday-party-pay [rates userrate eventprice]
  "calculate party-pay for birthday. Use rates"
  (* (/ eventprice (reduce + rates)) userrate)
  )

(defn add-event [{{ename :name price :price date :date :as params} :params}]
  "Add event in events table. Just create, without participantion."

  (vld/clear-errors!)
  (vld/rule (vld/has-value? ename) [:ename "Event name should not be empty"])
  (vld/rule (vld/greater-than? price 0) [:eprice "Event price should be greater than 0"])
  (vld/rule (vld/has-value? date) [:edate "Event should have date"])

  (if-not
    (vld/errors? :ename :eprice :edate) (do (db/add-event ename price date) (redirect "/user"))
    (handle/index))
  )

(defn addevent [ {event :name 
                  price :price 
                  date :date 
                  users :participants :as params} ]
  "Add event in events table, with adding participants, and calculating debts."
  (vld/clear-errors!)
  (vld/rule (vld/has-value? event) [:ename "Event name should not be empty"])
  (vld/rule (vld/greater-than? price 0) [:eprice "Event price should be greater than 0"])
  (vld/rule (vld/has-value? date) [:edate "Event should have date"])

  
  (let [ user-rates (db/get-rates users)
        party-pay (/ (read-string price) (reduce + user-rates))
        ]
    (db/add-event event (read-string price) date)
    (map #(db/credit-payment % event (* party-pay (db/get-rate %))) users)
    
    )
  )
