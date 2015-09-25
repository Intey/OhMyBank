(ns ombs.db.admin
  (:require [clojure.set :refer [rename-keys]]
            [korma.db :as kdb]
            [korma.core :as sql]
            [ombs.dbold :refer :all] ; for entity
            [ombs.db.payment :as p]
            [ombs.funcs :as fns]
            ))

; like pays, but for unconfirmed pays

(declare rm-fee)
(declare read-fee)

(defn affirm [fee-id]
  (kdb/transaction
    (apply p/debit-payment (read-fee fee-id))
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

(defn get-role [uid]
  (:role (first (sql/select users (sql/where {:id uid}) (sql/fields :role)))))
