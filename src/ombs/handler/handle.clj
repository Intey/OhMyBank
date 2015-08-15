(ns ombs.handler.handle
  (:require
    [ombs.view.pages :as pages]
    [ombs.core :as core]
    [ombs.db :as db]
    [ombs.validate :as isvalid]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [noir.validation :as vld]
    ))

(defn index [& [params]]
  "Handler. show index page with events."
  (pages/index)
  )

(defn user [& _]
  (if-let [username (sess/get :username)] ; if user logged
    (pages/user username)
    (redirect "/"))
  )

;=========================================== actions on user page ===========================================

(defn pay [{ename :event-name date :date :as params}]
  "Add participation of current user and selected event(given as param from post)"
  (let [uname (sess/get :username)
        uid (db/get-uid uname)
        eid (db/get-eid ename date)]
    (when (isvalid/ids? eid uid) 
      (db/debit-payment uid eid (db/get-debt uname ename date)) ))
  (redirect "/user")) ; go to user page in any case

(defn participate [{ename :event-name date :date price :price }]
  "Add participation of current user and selected event(given as param from post)"
  (let [uname (sess/get :username)]  
    (when (isvalid/participation? ename date uname)
      (core/add-participant ename date uname)))
  (redirect "/user")); go to user page in any case

(defn start [{ename :event-name date :date}]
  ;isvalid: not started, exists.
  (core/start-event ename date)
  (redirect "/user"); go to user page in any case
  )
