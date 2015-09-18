(ns ombs.handler.addevent
  (:require
    [ombs.dbold :as db]
    [ombs.db.payment :as dbpay]
    [ombs.core :as core]
    [ombs.funcs :as funcs]
    [ombs.validate :as isvalid]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [ombs.view.pages :refer [addevent] :rename {addevent addevent-page}]
    )
  )

(declare add-solid-event)
(declare add-partial-event)
(defn init-event [ {event :name price :price date :date parts :parts
                    users :participants
                    :as params} ]
  "Main function for creating new event."
  (if (isvalid/new-event? event price date)
    (do
      (if (nil? (funcs/parse-int parts))
        (add-solid-event params)
        (add-partial-event (update params :parts funcs/parse-int)))
      (redirect "/user"))
    ;if validation fails
    (addevent-page (db/get-usernames)) ))

;FIXME:
; issue, when we check one user as participant, so there, users - is value(string). When >1, users - vector.
; Solutions:
; * Form should return always vector
; * convert value to vector
(defn- add-solid-event [ {event :name price :price date :date
                          users :participants
                          :as params} ]
  "Add event in events table, with adding participants, and calculating debts."
  (println "add solid event")
  (if (isvalid/new-event? event price date)
    (do
      (db/add-event event (read-string price) (sess/get :username) date)
      (if (> (count users) 0)
        (let [party-pay (core/party-pay price users)]
          ;use 'dorun' for execute lazy function 'db/credit-payment'
          (dorun (map #(dbpay/credit-payment event date % party-pay)
                      (funcs/as-vec users))))) ; may have only one user, so create vec
      true) ; all is ok
    false)) ; validation fail

(declare add-good)
(defn- add-partial-event [{event :name price :price date :date parts :parts
                           users :participants
                           :as params}]
  (println "add partial event")
  (if (isvalid/new-event? event price date)
    (do
      (db/add-event event (funcs/parse-int price) (sess/get :username) date parts)
      (add-good params)
      true)
    false))

(defn- add-good [ {event :name price :price date :date users :participants parts :parts :as params} ]
  (if (db/add-goods event date parts)
    (redirect "/user")
    (addevent-page (db/get-usernames)) ))

