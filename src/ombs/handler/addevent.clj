(ns ombs.handler.addevent
  (:require
    [ombs.db.old :as db]
    [ombs.db.partial :as partial-event]
    [ombs.db.payment :as dbpay]
    [ombs.core :as core]
    [ombs.funcs :as funcs]
    [ombs.validate :as isvalid]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [ombs.view.pages :refer [addevent] :rename {addevent addevent-page}]
    )
  )
; Forward declarations
(declare add-solid-event)

(declare add-partial-event)
(declare add-good)

(declare add-participants)
; ====== End forward declarations

(defn init-event [ {event :name price :price date :date parts :parts
                    users :participants
                    :as params} ]
  "Main function for creating new event."
  (if (isvalid/new-event? event price date)
    (do
      (if (zero? (funcs/parse-int parts))
        (add-solid-event params)
        (add-partial-event (update params :parts funcs/parse-int)))
      (when (not-empty users) (add-participants params))
      (redirect "/user"))
    ;if validation fails
    (addevent-page (db/get-usernames)) ))

(defn- add-solid-event [ {event :name price :price date :date
                          users :participants
                          :as params} ]
  "Add event in events table, with adding participants, and calculating debts."
  (if (isvalid/new-event? event price date)
    (do
      (db/add-event event (read-string price) (sess/get :username) date)
      (if (> (count users) 0)
        (let [party-pay (core/party-pay (funcs/parse-int price) users)]
          ;use 'dorun' for execute lazy function 'db/credit-payment'
          (dorun (map #(dbpay/credit-payment (db/get-eid event date) (db/get-uid %) party-pay)
                      (funcs/as-vec users))))) ; may have only one user, so create vec
      true) ; all is ok
    false)) ; validation fail

(defn- add-partial-event [{event :name price :price date :date parts :parts
                           users :participants
                           :as params}]
  (if (isvalid/new-event? event price date)
    (do
      (db/add-event event (funcs/parse-int price) (sess/get :username) date parts)
      (add-good params)
      true)
    false))

(defn- add-good [ {event :name price :price date :date users :participants parts :parts :as params} ]
  (if (partial-event/add-goods event date parts)
    (redirect "/user")
    (addevent-page (db/get-usernames)) ))

(defn- add-participants [{event :name date :date users :participants}]
  (dorun (map (comp #(dbpay/add-participant % (db/get-eid event date)) db/get-uid) users)))
