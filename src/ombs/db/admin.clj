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
              (sql/fields :id :money :date :parts)  
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
(defn write-pay [{eid :events_id uid :users_id date :date parts :parts}]
    (when (isvalid/ids? eid uid)
      (if (> 0 parts) 
        (process-it eid parts uid) 
        (dbpay/debit-payment uid eid (dbpay/get-debt uid eid)))
      (if (can-finish? eid)
        (finish eid)))  )

(defn process-it [eid parts uid]
  ;But, it have many check on adding 
  ;(if (isvalid/parts? ename date parts) ; its check if parts >= than free parts. 
  (kdb/transaction
    (dbpay/credit-payment eid uid (parts-price eid parts)) ; on each debit, we should have credit.
    (dbpay/debit-payment eid uid (parts-price eid parts))
    (shrink-goods eid parts)))
