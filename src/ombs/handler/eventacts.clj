(ns ombs.handler.eventacts
  (:require
    [ombs.core :as core]
    [ombs.db.event :as dbe]
    [ombs.db.user :as dbu]
    [ombs.db.payment :as dbpay]
    [ombs.validate :as isvalid]
    [ombs.funcs :as f]
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
        parts (f/parse-int parts)]
    (if (isvalid/payment? eid uid parts)
      (do
        (dbpay/create-fee uid eid parts)
        (json/generate-string {:ok "Waiting confirmation..."}))
      (json/generate-string {:error (isvalid/errors-string)})
      )))

(defn add-in-progress [uid eid]

  )

(defn participate [{eid :eid ename :event-name date :date :as params}]
  "Add participation of current user and selected event(given as param from
  post)"
  (if-let [uname (sess/get :username)]
    (when (isvalid/participation? eid)
      (if (dbe/is-initial? eid)
        (dbpay/add-participant (dbu/get-uid uname) eid)
        (add-in-progress (dbu/get-uid uname) eid)))))


(defn start [{eid :eid ename :event-name date :date :as params}]
  (if (dbe/is-initial? eid)
    (dbe/set-status eid :in-progress)
    (let [users (dbpay/get-participants eid)
          party-pay (f/party-pay (:price (dbe/get-event eid)) users)]
      (if (= (dbe/get-parts eid) 0); create debts only when event not partial
        (doall (map #(dbpay/credit-payment eid (dbu/get-uid %) party-pay) users))))))
