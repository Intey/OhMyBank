
(ns ombs.handler.addevent
  (:require 
    [ombs.handler :as handle]
    [ombs.db :as db]
    [ombs.view.common :as vc]
    [ombs.core :as core]
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

;FIXME:
; issue, when we check one user as participant, so there, users - is value(string). When >1, users - vector.
; Solutions:
; * Form should return always vector
; * convert value to vector
(defn addevent [ {event :name 
                  price :price 
                  date :date 
                  users :participants :as params} ]
  "Add event in events table, with adding participants, and calculating debts."
  ;valudation
  (vld/clear-errors!)
  (vld/rule (vld/has-value? event) [:ename "Event name should not be empty"])
  (vld/rule (vld/greater-than? price 0) [:eprice "Event price should be greater than 0"])
  (vld/rule (vld/has-value? date) [:edate "Event should have date"])
  (vld/rule (empty? (db/get-event event date)) [:event "Event with same name today was created. Use another name"])
  ;end validation
  (println (empty? (db/get-event event date)))
  (if-not (vld/errors? :ename :eprice :edate :event) 
    (let [user-rates (db/get-rates (core/as-vec users))
          party-pay (/ (read-string price) (reduce + user-rates))]

      (db/add-event event (read-string price) date)
      (dorun (map 
               #(db/credit-payment % event (* party-pay (db/get-rate %))) 
               (core/as-vec users)))
      (redirect "/addevent"))

    (handle/index))

  )
