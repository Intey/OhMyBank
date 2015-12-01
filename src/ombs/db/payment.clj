(ns ombs.db.payment
  (:require [korma.db :as kdb]
            [korma.core :as sql]
            [ombs.dbold :refer :all]
            [ombs.funcs :as f]
            ))

(defn participated?
  "Check participation in event. If user have some payment action on event -
  he is participated."
  ([uid eid]
   (not (empty? (sql/select participation
                            (sql/where {:users_id uid
                                        :events_id eid})))))

  ([uname ename edate]
   (participated? (get-uid uname) (get-eid ename edate))))

(defn get-debt
  "sum of all credits and debits in pays for current user and event. "
  ; full debt
  ([username]
   (if-let [summary (:debt (first (sql/select debts
                                              (sql/where {:user username})
                                              (sql/aggregate (sum :debt)
                                                             :debt))))]
     summary
     0.0))
  ; debt on some event
  ([username event date]
   (if-let [summary (:debt (first (sql/select debts
                                              (sql/where {:user username
                                                          :event event
                                                          :date date}))))]
     summary
     0.0))
  ([uid eid]
   (if-let [summary (:debt (first (sql/select debts
                                              (sql/where {:eid eid
                                                          :uid uid}))))]
     summary
     0.0))
  )

(defn credit-payment [eid uid money]
  (sql/insert pays (sql/values {:users_id uid
                                :events_id eid
                                :credit money })))

(defn debit-payment [eid uid money]
  (sql/insert pays (sql/values {:users_id uid
                                :events_id eid
                                :debit money })))

(defn add-participant
  ([event date user]
  (sql/insert participation (sql/values {:events_id (get-eid event date)
                                         :users_id (get-uid user)})))
  ([uid eid]
   (sql/insert participation (sql/values {:events_id eid :users_id uid })))
  )

(defn get-participants
  ([ename edate] (get-participants (get-eid ename edate)))
  ([eid] (mapv #(first (vals %)) (sql/select participants (sql/fields :user)
                                      (sql/where {:eid eid})))))

(defn calc-fee-money [uid eid parts]
  (if (> parts 0)
    (parts-price eid parts)
    (get-debt uid eid)) )

(defn create-fee
  ([uid eid] (create-fee uid eid 0))
  ([uid eid parts]
   (sql/insert fees (sql/values {:users_id uid
                                 :events_id eid
                                 :parts parts
                                 :money (calc-fee-money uid eid parts) }))))

(declare fees-money)
(defn can-pay? [uid eid]
  (and
    (participated? uid eid)
    (= (get-status eid) (:in-progress statuses))
    (< (fees-money uid eid) (/ (get-price eid) (count (get-participants eid)))))
  )

(defn fees-money [uid eid]
  "Get sum of fees of some user on some event."
  (f/nil-fix (:summ (first (sql/select fees
                                       (sql/where {:users_id uid :events_id eid})
                                       (sql/fields :summ)
                                       (sql/aggregate (sum :money) :summ))))))
