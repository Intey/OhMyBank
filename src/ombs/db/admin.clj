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

(defn fees-money [uid eid]
  "Get sum of fees of some user on some event."
  (:summ (first (sql/select fees
              (sql/where {:users_id uid :events_id eid})
              (sql/fields :summ)
              (sql/aggregate (sum :money) :summ)))))

(defn get-role [uid]
  (:role (first (sql/select users (sql/where {:id uid}) (sql/fields :role)))))

; ============================ PRIVATE =======================================

(defn- write-pay [{eid :events_id uid :users_id parts :parts money :money}]
    (println "write-pay eid:" eid " uid:" uid " parts:" parts " money:" money)
    (when (isvalid/ids? eid uid)
      (when (> parts 0)
        (do
          (println " Pay partial event")
          (shrink-goods eid parts)
          (dbpay/credit-payment eid uid money)))
      (dbpay/debit-payment eid uid money)
      (if (can-finish? eid)
        (finish eid))))

(defn- get-fee [id] (first (sql/select fees)))

(defn- rm-fee [id] (sql/delete fees (sql/where {:id id})))
