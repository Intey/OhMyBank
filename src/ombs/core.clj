(ns ombs.core
  "Contains main logic. No validations. All function hope that you give to it valid data. Use in
  handlers, views, etc. "
  (:require [ombs.db.old :as db]
            [ombs.db.payment :as dbpay]
            [ombs.db.admin :as db-adm]
            [ombs.funcs :as fns]
            [ombs.validate :refer [add-error]]
            [noir.response :refer [redirect]]
            ))

(defn rate [student?] (if (= student? "on") 0.5 1.0 ) )

(defn events [] (db/get-events [:initial :in-progress]))

(defn participated? [uname ename edate] (dbpay/participated? uname ename edate))

(defn debt
  "return debt count for user on event. If it's partial event, so ve use on the fly calculations."
  ([username] (dbpay/get-debt username)) ; full user debt on all events
  ([username event date] (dbpay/get-debt username event date)))

(defn party-pay [event-price users]
  "Simple for common events. For birthday, need more complex realization depends on each user rate."
  (fns/part-price event-price (count users)))

(defn is-initial?
  ([ename date] (db/is-initial? (db/get-eid ename date)))
  ([eid] (db/is-initial? eid)))

(defn is-active? [ename date] (= (db/get-status ename date) (:in-progress db/statuses)))

(defn- add-in-progress
  [uid eid]
  (add-error :participation "No implementation for participate user in-progress event")
  (redirect "/user")
  )

(defn add-participant
  ([ename date uname] (add-participant (db/get-eid ename) (db/get-uid uname)))
  ([eid uid]
   (if (db/is-initial? eid)
     (dbpay/add-participant uid eid)
     (add-in-progress uid eid))))

(defn start-event [eid]
  (db/set-status eid :in-progress)
  (let [users (dbpay/get-participants eid)
        party-pay (party-pay (:price (db/get-event eid)) users)]
    (if (= (db/get-parts eid) 0); create debts only when event not partial
      (doall (map #(dbpay/credit-payment eid (db/get-uid %) party-pay) users)))))

(defn participants-count [ename date]
  (count (dbpay/get-participants ename date)))

(defn fee-exist? [eid uid]
  (not (empty? (db-adm/find-fee eid uid))))

(defn- ^:deprecated extract-event [m]
  "Extract event keys from raw result of query participated-list."
  (select-keys m '(:name :price :date :author)))

(defn- ^:deprecated grouper [events]
  "Reorganize participation result to map, where key - is event, and value - vector of users, that
  participate this event. Expect input, after using group-by on BD-table 'participants':
  (event-name, event-price, date, remain, user). Each row, can contains same event, with different users"
  (map (fn [[k v]] {:event k :users (mapv :user v)}) ;this func map usernames in vector
       (group-by extract-event events)))


