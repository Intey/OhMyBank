(ns ombs.db.admin
  (:require [clojure.set :refer [rename-keys]]
            [korma.db :as kdb]
            [korma.core :as sql]
            [noir.session :as sess]
            [ombs.dbold :refer :all] ; for entity
            [ombs.db.payment :as dbpay]
            [ombs.validate :as isvalid]
            [ombs.funcs :as fns]
            ))

; like pays, but for unconfirmed pays

(declare rm-fee)
(declare read-fee)
(declare event-from-fee)
(defn affirm [fee-id]
  "Realize fee, and if it's last part of payment - close it."
  (kdb/transaction
    (apply dbpay/debit-payment (read-fee fee-id))
    (if (apply can-finish? (event-from-fee fee-id))
      (finish (event-from-fee fee-id)))
    (rm-fee fee-id)))

(defn refute [fee-id]
  "Alias for rm-fee, to avoid call refute in affirm, or rm-fee in interface."
  (rm-fee fee-id) )

(defn get-fees [] 
  (sql/select fees 
              (sql/fields :money :date)  
              (sql/with users  (sql/fields [:name :user])) 
              (sql/with events (sql/fields [:name :event] [:date :edate])) 
              ))

; pivate part
(defn- rm-fee [id] (sql/delete fees (sql/where {:id id})))
(defn- read-fee [id]
      (replace
        (first (sql/select fees (sql/where {:id id})))
        [:users_id :events_id :money]))
(defn- event-from-fee [id]
  (take 2 (read-fee id)))

(defn get-role [uid]
  (:role (first (sql/select users (sql/where {:id uid}) (sql/fields :role)))))

(declare process-it)
(defn pay [{ename :event-name date :date :as params}]
  "Add payment record in db. For current user.
  Also, if this action, make summary event debt = 0, finish it"
  (println "pay solid")
  (let [uname (sess/get :username)
        uid (get-uid uname)
        eid (get-eid ename date)]
    (when (isvalid/ids? eid uid)
      (dbpay/debit-payment uid eid (dbpay/get-debt uname ename date))
      (if (can-finish? ename date)
        (finish ename date)))))

(declare process-it)
(defn pay-part [{ename :event-name date :date parts :parts :as params}]
  "Add participation of current user and selected event(given as param from
  post). Parts in params is count of parts, that user want to pay"
  (println "pay partial")
  (let [uname (sess/get :username)
        uid (get-uid uname)
        eid (get-eid ename date)
        parts (fns/parse-int parts)]
    (when (isvalid/ids? eid uid)
      (process-it ename date parts uname)
      (dbpay/debit-payment uid eid (dbpay/get-debt uname ename date))
      (when (= 0 (get-rest-parts ename date))
        (finish ename date)))
  ))

(defn process-it [ename date parts uname]
  (if (isvalid/parts? ename date parts) ; its check if parts >= than free parts
    (do
      (dbpay/credit-payment ename date uname (parts-price ename date parts)) ; fix database logic
      (dbpay/debit-payment (get-uid uname) (get-eid ename date) (parts-price ename date parts))
      (shrink-goods ename date parts))))
