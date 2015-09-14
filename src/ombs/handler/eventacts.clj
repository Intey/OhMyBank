(ns ombs.handler.eventacts
  (:require
    [ombs.core :as core]
    [ombs.dbold :as db]
    [ombs.db.payment :as dbpay]
    [ombs.validate :as isvalid]
    [ombs.funcs :refer [parse-int]]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    ))

(defn- finish [ename date] (db/set-status ename date :finished))

(declare process-it)

(defn pay [{ename :event-name date :date :as params}]
  "Add participation of current user and selected event(given as param from 
  post). Parts in params is count of parts, that user want to pay"
  (println "call pay with: " ename " " date)
  (let [uname (sess/get :username)
        uid (db/get-uid uname)
        eid (db/get-eid ename date)]
    (when (isvalid/ids? eid uid) 
      (dbpay/debit-payment uid eid (dbpay/get-debt uname ename date)) 
      (if (db/can-finish? ename date) 
        (finish ename date)))) 
  (redirect "/user")) ; go to user page in any case

(defn pay-part [{ename :event-name date :date parts :parts :as params}]
  "Add participation of current user and selected event(given as param from 
  post). Parts in params is count of parts, that user want to pay"
  (println "call pay-parts with: " ename " " date " '" parts "'")
  (let [uname (sess/get :username)
        uid (db/get-uid uname)
        eid (db/get-eid ename date)
        parts parts]
    (when (isvalid/ids? eid uid) 
      (if (>= parts 1)
        (process-it ename date parts uname)
        (dbpay/debit-payment uid eid (dbpay/get-debt uname ename date))) 
      (when (= 0 (db/get-rest-parts ename date))
        (finish ename date) ))) 
  (redirect "/user")) ; go to user page in any case

(defn process-it [ename date parts uname]
  (println "processing " ename " " date " " parts " " uname)
  (when (isvalid/parts? ename date parts) ; check if parts >= than free parts
    (dbpay/debit-payment (db/get-uid uname) (db/get-eid ename date) (core/parts-price ename date parts))
    (db/shrink-goods ename date parts)))

(defn participate [{ename :event-name date :date}]
  "Add participation of current user and selected event(given as param from
  post)"
  (let [uname (sess/get :username)]  
    (when (isvalid/participation? ename date uname)
      (core/add-participant ename date uname)))
  (redirect "/user")); go to user page in any case

(defn start [{ename :event-name date :date}]
  ;isvalid: not started, exists.
  (core/start-event ename date)
  (redirect "/user")); go to user page in any case 
