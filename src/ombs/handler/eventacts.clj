(ns ombs.handler.eventacts
  (:require
    [ombs.core :as core]
    [ombs.db.event :as dbe]
    [ombs.db.user :as dbu]
    [ombs.db.payment :as dbpay]
    [ombs.validate :as isvalid]
    [ombs.funcs :refer [parse-int]]
    [cheshire.core :as json]
    [noir.session :as sess]
    ))

(defn- finish [ename date] (dbe/set-status ename date :finished))

(defn pay [{eid :eid ename :event-name date :date parts :parts :as params}]
  "Add participation of current user and selected event(given as param from
  post). Parts in params is count of parts, that user want to pay"
  (println "Pay params: " params)
  (let [uname (sess/get :username)
        uid (dbu/get-uid uname)
        parts (parse-int parts)]
    (if (isvalid/payment? eid uid parts)
      (do
        (dbpay/create-fee uid eid parts)
        (json/generate-string {:ok "Waiting confirmation..."}))
      (json/generate-string {:error (isvalid/errors-string)})
      )))

(defn participate [{eid :eid ename :event-name date :date :as params}]
  "Add participation of current user and selected event(given as param from
  post)"
  (if-let [uname (sess/get :username)]
    (when (isvalid/participation? eid)
      (core/add-participant eid (dbu/get-uid uname)))))

(defn start [{eid :eid ename :event-name date :date :as params}]
  (if (dbe/is-initial? eid)
    (core/start-event eid)))
