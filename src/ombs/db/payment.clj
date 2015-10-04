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
   (if-let [summary (:debt (first (sql/select debts (sql/where {:eid eid :uid uid}))))]
     summary
     0.0)) 
  )

(defn credit-payment [eid uid money]
  (println (str "credit " uid " user " uid " money " money ))
  (sql/insert pays (sql/values { :users_id uid :events_id eid :credit money })))

(defn debit-payment [eid uid money]
  (println (str "dedit " uid " user " uid " money " money ))
  (sql/insert pays (sql/values { :users_id uid :events_id eid :debit money }))
  )

(defn add-participant [event date user]
  (sql/insert participation (sql/values {:events_id (get-eid event date) :users_id (get-uid user)})))

(defn get-participants [ename edate]
  (mapv #(first (vals %)) (sql/select participants (sql/fields :user)
                                      (sql/where {:event ename :date edate}))))

(defn calc-fee-money [uid eid parts]
  (if (> parts 0) 
    (parts-price eid parts)
    (get-debt uname ename date)) )

(defn create-fee [uname ename date & [parts]] 
  (let [uid (get-uid uname)
        eid (get-eid ename date) ]
    (sql/insert fees (sql/values {:users_id uid
                                  :events_id eid
                                  :parts parts
                                  :money (calc-fee-money uid eid parts) }))))
