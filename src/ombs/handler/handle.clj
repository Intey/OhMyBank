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
    (pages/user (core/event-users))
    (redirect "/"))
  )

(defn pay [{ename :event-name date :date price :price }]
  "Add participation of current user and selected event(given as param from post)"
  (let [uname (sess/get :username)
        uid (db/get-uid uname)
        eid (db/get-eid ename date)]
    (isvalid/stake? eid uid)
    ))
