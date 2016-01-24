(ns ombs.db.payment
  (:require [korma.db :as kdb]
            [korma.core :as sql]
            [ombs.db.old :refer :all]
            [ombs.db.event :as dbe]
            [ombs.db.user :as dbu]
            [ombs.db.partial :as partial-event]
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
   (participated? (dbu/get-uid uname) (dbe/get-eid ename edate))))

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

;; get all participants
;; calc old_party-pay
;; calc new_party-pay (participants + new)
;; create credit-payment for new participant
;; create debits, with diff price for rest
;; diff:
;; old_party-pay - new_party-pay
(defn add-participant
  ([event date user]
   )
  ([uid eid]
   (sql/insert participation (sql/values {:events_id eid :users_id uid })))
  )

;; calc new_party-pay
;; create credits
(defn add-participants [event users]
  (let [party-pay (f/party-pay (f/parse-int (:price event)) users)]
    ;use 'dorun' for execute lazy function 'db/credit-payment'
    (dorun
      (map #(comp
              (credit-payment (:id event) (dbu/get-uid %) party-pay)
              (sql/insert participation
                          (sql/values {:events_id (:id event)
                                       :users_id (dbu/get-uid %)})))
           (f/as-vec users))))) ; may have only one user, so create vec)

(defn get-participants
  ([ename edate] (get-participants (dbe/get-eid ename edate)))
  ([eid] (mapv #(first (vals %)) (sql/select participants (sql/fields :user)
                                      (sql/where {:eid eid})))))

(defn calc-fee-money [uid eid parts]
  (if (> parts 0)
    (partial-event/parts-price eid parts)
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
    (= (dbe/get-status eid) (:in-progress dbe/statuses))
    (< (fees-money uid eid) (/ (dbe/get-price eid) (count (get-participants eid)))))
  )

(defn fees-money [uid eid]
  "Get sum of fees of some user on some event."
  (f/nil-fix (:summ (first (sql/select fees
                                       (sql/where {:users_id uid :events_id eid})
                                       (sql/fields :summ)
                                       (sql/aggregate (sum :money) :summ))))))

(defn get-feesed-parts [eid]
  "Select sum of parts, that contained in fees for given event id."
  (:sum (first (sql/select fees
                           (sql/where {:events_id eid})
                           (sql/aggregate (sum :parts) :sum)))))

(defn free-parts [eid]
  "Return num of parts, that can be payed."
  (- (f/nil-fix (partial-event/rest-parts eid))
     (f/nil-fix (get-feesed-parts eid))))

(defn- price-diff
  ([ename date]
  (reduce -
          (replace
            (first (sql/select summary (sql/where {:event ename :date date})
                               (sql/aggregate (sum :debits) :debits)
                               (sql/aggregate (sum :credits) :credits)))
            [:debits :credits])))
  ([eid]
  (reduce -
          (replace
            (first (sql/select summary (sql/where {:eid eid})
                               (sql/aggregate (sum :debits) :debits)
                               (sql/aggregate (sum :credits) :credits)))
            [:debits :credits])))

  )

(defn can-finish?
  ([ename date]
   (zero?
     (if (zero? (dbe/get-parts ename date))
       (price-diff ename date)
       (partial-event/rest-parts ename date))))
  ([eid]
   (zero?
     (if (zero? (dbe/get-parts eid))
       (price-diff eid)
       (partial-event/rest-parts eid)))))
