(ns ombs.db.payment
  (:require [korma.db :as kdb]
            [korma.core :as sql]
            [ombs.dbold :refer :all]
            ))

(defn participated?
  "Check participation in event. If user have some payment action on event - he is participated."
  ([uid eid]
   (not (empty? (sql/select participation (sql/where (and (= :users_id uid) (= :events_id eid)))))))

  ([uname ename edate]
   (participated? (get-uid uname) (get-eid ename edate))))

(defn get-debt
  "sum of all credits and debits in pays for current user and event. "
  ; full debt
  ([username]
   (if-let [summary (:debt (first (sql/select debts (sql/where {:user username}) (sql/aggregate (sum :debt) :debt))))]
     summary
     0.0))
  ; debt on some event
  ([username event date]
   (if-let [summary (:debt (first (sql/select debts (sql/where {:user username :event event :date date}))))]
     summary
     0.0))
  ([uid eid]
   (if-let [summary (:debt (first (sql/select debts (sql/where {:eid eid :uid uid})
                                              (sql/with events (sql/fields [:id :eid])) 
                                              (sql/with users (sql/fields [:id :uid])))))]
     summary
     0.0)) 
  )

(defn credit-payment [uid eid money]
  (println (str "credit " uid " user " uid " money " money ))
  (sql/insert pays (sql/values { :users_id uid :events_id eid :credit money })))

(defn debit-payment [uid eid money]
  (sql/insert pays (sql/values { :users_id uid :events_id eid :debit money }))
  )

(defn add-participant [event date user]
  (sql/insert participation (sql/values {:events_id (get-eid event date) :users_id (get-uid user)}))
  )

(defn get-participants [ename edate]
  (mapv #(first (vals %)) (sql/select participants (sql/fields :user)
                                      (sql/where {:event ename :date edate}))))

(defn create-fee [uname ename date & [parts]] 
  (sql/insert fees (sql/values {:users_id (get-uid uname) :events_id (get-eid ename date) 
                                :parts parts
                                :money (if parts 
                                         (parts-price (get-eid ename date) parts)
                                         (get-debt uname ename date)) } )))
