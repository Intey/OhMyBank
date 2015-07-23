(ns ombs.handler.handle
  (:require
    [ombs.view.pages :as pages]
    [ombs.db :as db]
    [ombs.core :as core]
    [ombs.validate :as isvalid]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [noir.validation :as vld]
    ))

(defn print-it
  "just print request. Debug method."
  [request]
  (str request))

(defn index [& [params]]
  "Handler. show index page with events."
  (pages/index (assoc params :events (db/get-events-list)))
  )

(defn user [& _]
  (if-let [username (sess/get :username)] ; if any user logged
    (pages/user (core/stakes))
    (redirect "/"))
  )

(defn pay [{ename :event-name date :date debt :debt :as params}]
  "Add participation of current user and selected event(given as param from post)"
  (let [uname (sess/get :username)
        uid (db/get-uid uname)
        eid (db/get-eid ename date)]
    (if (isvalid/stake? eid uid)
      (db/debit-payment uid eid (db/get-debt uname ename date))
      "validation fails."
      )
    (redirect "/user")
    ))
(defn participate [{ename :event-name date :date price :price }]
  "Add participation of current user and selected event(given as param from post)"
  (let [uname (sess/get :username)
        uid (db/get-uid uname)
        eid (db/get-eid ename date)]
    (if (isvalid/stake? eid uid)
      "participayed ^_^ . Joke, Just not realized."
      "validation fails."
      )

    ))
