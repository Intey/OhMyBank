(ns ombs.handler.addevent
  (:require
    [noir.session :as sess]
    [cheshire.core :as json]
    [ombs.db.event :as dbe]
    [ombs.db.user :as dbu]
    [ombs.db.partial :as partial-event]
    [ombs.db.payment :as dbpay]
    [ombs.core :as core]
    [ombs.funcs :as funcs]
    [ombs.validate :as isvalid]
    [ombs.handler.api :refer [okRes errorRes] ]
    )
  )
; Forward declarations
(declare add-solid-event)

(declare add-partial-event)
(declare add-good)

(declare add-participants)
; ====== End forward declarations
(defrecord Ok [code data])
(defn init-event [ {event :name price :price date :date parts :parts
                    users :participants
                    :as params} ]
  "Main function for creating new event."
  (if (isvalid/new-event? event price date)
    (do
      (if (zero? (funcs/parse-int parts))
        (add-solid-event params)
        (add-partial-event (update params :parts funcs/parse-int)))
      (dbpay/debit-payment (dbe/get-eid event date)
                           (dbu/get-uid (sess/get :username))
                           (read-string price))
      (when (not-empty users) (add-participants params))
      okRes)
    ;if validation fails
    ))

(defn- add-solid-event [ {event :name price :price date :date
                          users :participants
                          :as params} ]
  "Add event in events table, with adding participants, and calculating debts."
  (dbe/add-event event (read-string price) (sess/get :username) date)
  (if (> (count users) 0)
    (let [party-pay (core/party-pay (funcs/parse-int price) users)]
      ;use 'dorun' for execute lazy function 'db/credit-payment'
      (dorun (map #(dbpay/credit-payment (dbe/get-eid event date) (dbu/get-uid %) party-pay)
                  (funcs/as-vec users)))))); may have only one user, so create vec

(defn- add-partial-event [{event :name price :price date :date parts :parts
                           users :participants
                           :as params}]
  (dbe/add-event event (funcs/parse-int price) (sess/get :username) date parts)
  (add-good params))

(defn- add-good [ {event :name price :price date :date users :participants parts :parts :as params} ]
  (if (partial-event/add-goods event date parts)
    (json/generate-string {:ok true})
    ;valid response
    ;invalide response
    ))

(defn- add-participants [{event :name date :date users :participants}]
  (dorun (map (comp #(dbpay/add-participant % (dbe/get-eid event date)) dbu/get-uid) users)))
