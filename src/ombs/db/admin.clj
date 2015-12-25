(ns ombs.db.admin
  (:require [clojure.set :refer [rename-keys]]
            [korma.db :as kdb]
            [ombs.db.payment :as dbpay]
            [ombs.db.partial :as partial-event]
            [korma.core :as sql]
            [noir.session :as sess]
            [ombs.db.old :refer :all] ; for entity
            [ombs.validate :as isvalid]
            [ombs.funcs :as fns]
            ))

(declare rm-fee)
(declare get-fee)
(declare write-pay)
(defn affirm [fee-id]
  "Realize fee, and if it's last part of payment - close it."
  (kdb/transaction
    (write-pay (get-fee fee-id))
    (rm-fee fee-id)))

(defn refute [fee-id]
  "Alias for rm-fee, to avoid call refute in affirm, or rm-fee in interface."
  (rm-fee fee-id) )

(defn get-fees []
  "Return fees, for views"
  (sql/select fees (sql/fields :id :money :date :parts)
              (sql/with users  (sql/fields [:name :user]))
              (sql/with events (sql/fields [:name :event] [:date :edate]))))

(defn get-fid [uid eid]
  (:id (first
         (sql/select fees (sql/fields :id)
                     (sql/where {:users_id uid :events_id eid})))))

(defn find-fee [eid uid]
  (sql/select fees (sql/where {:users_id uid :events_id eid})))

(defn finish
  ([ename date] (set-status ename date :finished))
  ([eid] (set-status eid :finished)))

; ============================ PRIVATE =======================================

(defn- write-pay [{eid :events_id uid :users_id parts :parts money :money}]
    (when (isvalid/payment? eid uid parts)
      (when (> parts 0)
        (partial-event/shrink-goods eid parts)
        (dbpay/credit-payment eid uid money))
      (dbpay/debit-payment eid uid money)
      (if (dbpay/can-finish? eid)
        (finish eid))))

(defn- get-fee [id]
  (first (sql/select fees (sql/where {:id id}))) )

(defn- rm-fee [id]
  (sql/delete fees (sql/where {:id id})))

