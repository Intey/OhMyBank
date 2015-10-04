(ns ombs.handler.eventacts
  (:require
    [ombs.core :as core]
    [ombs.dbold :as db]
    [ombs.db.payment :as dbpay]
    [ombs.validate :as isvalid]
    [ombs.funcs :refer [parse-int]]
    [ombs.handler.pages :as pages]
    [noir.session :as sess]
    ))

(defn- finish [ename date] (db/set-status ename date :finished))

(defn pay [{ename :event-name date :date parts :parts :as params}]
  "Add participation of current user and selected event(given as param from
  post). Parts in params is count of parts, that user want to pay"
  (let [uname (sess/get :username)
        uid (db/get-uid uname)
        eid (db/get-eid ename date)
        parts (parse-int parts)]
    (dbpay/create-fee uid eid parts) 
  (pages/user))) ; go to user page in any case

(defn participate [{ename :event-name date :date}]
  "Add participation of current user and selected event(given as param from
  post)"
  (let [uname (sess/get :username)]
    (when (isvalid/participation? ename date uname)
      (core/add-participant ename date uname)))
  (pages/user)); go to user page in any case

(defn start [{ename :event-name date :date}]
  ;isvalid: not started, exists.
  (core/start-event ename date)
  (pages/user)); go to user page in any case
