(ns ombs.handler.addevent
  (:require 
    [ombs.db :as db]
    [ombs.core :as core]
    [ombs.funcs :as funcs]
    [ombs.validate :as isvalid]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [ombs.view.pages :refer [addevent] :rename {addevent addevent-page}] 
    )
  )

(defn party-pay [participants-count eventprice]
  "Return party pay for regular event, not use rates."
  (/ eventprice participants-count)
  )


;FIXME:
; issue, when we check one user as participant, so there, users - is value(string). When >1, users - vector.
; Solutions:
; * Form should return always vector
; * convert value to vector
(defn- addevent [ {event :name price :price date :date users :participants parts :parts :as params} ]
  "Add event in events table, with adding participants, and calculating debts."
  ;valudation
  (if (isvalid/new-event? event price date) 
    (let [party-pay (core/party-pay price users)]
      (db/add-event event (read-string price) (sess/get :username) date parts)
      ;use 'dorun' for execute lazy function 'db/credit-payment'
      (dorun 
        (map 
          #(db/credit-payment event date % party-pay)
          (funcs/as-vec users)))
      (redirect "/user"))
    ;if validation fails
    (addevent-page (db/get-usernames)) ))

(defn init-event [ {event :name price :price date :date users :participants parts :parts :as params} ]
  "Add event in events table, with adding participants, and calculating debts."
  ;valudation
  (if (isvalid/new-event? event price date) 
    (do
      (db/add-event event (read-string price) (sess/get :username) date parts) 
      (redirect "/user"))
    ;if validation fails
    (addevent-page (db/get-usernames)) ))


(defn add-good [ {event :name price :price date :date users :participants parts :parts :as params} ]
  (addevent params)
  (db/add-goods event date parts) )
