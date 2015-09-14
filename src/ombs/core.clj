(ns ombs.core
  "Contains main logic. No validations. All function hope that you give to it valid data. Use in 
  handlers, views, etc. "
  (:require [ombs.dbold :as db]
            [ombs.db.payment :as dbpay]
            [ombs.funcs :as fns]
            [ombs.validate :refer [add-error]]
            [noir.response :refer [redirect]]
            ))

(defn rate [student?] (if (= student? "on") 0.5 1.0 ) )

(defn events [] (db/get-events))  

(defn participated? [uname ename edate] (dbpay/participated? uname ename edate))

(defn debt 
  "return debt count for user on event. If it's partial event, so ve use on the fly calculations."
  ([username] (dbpay/get-debt username)) ; full user debt on all events
  ([username event date] (dbpay/get-debt username event date)))

(defn part-price [event-price parts] (/ event-price parts))

(defn parts-price [ename date parts]
  (* parts (part-price (db/get-price ename date) (db/get-parts ename date))))   

(defn party-pay [event-price users]
  "Simple for common events. For birthday, need more complex realization depends on each user rate."
  (part-price event-price (count users)))


(defn is-initial? [ename date] (db/is-initial? ename date))
(defn is-active? [ename date] (= (db/get-status ename date) (:in-progress db/statuses)))

(defn- add-in-progress [ename date uname]
  (println "Add in progress!")
  (let [message (str "No implementation for participate user " uname " in-progress event " (db/get-event ename date) )]
    (add-error :participation message))
    (redirect "/user")
  )

(defn add-participant [ename date uname]
  "Hope, that data is ok, and given user can participate in given event."
  (println (str "core.add-participant:" uname " event:" ename " date:" date))
  (if (is-initial? ename date)
    (dbpay/add-participant ename date uname)
    (add-in-progress ename date uname)))

(defn start-event [ename edate]
  (db/set-status ename edate :in-progress)
  (let [users (dbpay/get-participants ename edate)
        party-pay (party-pay (:price (db/get-event ename edate)) users)]
    (if (= (db/get-parts ename edate) 0); create debts only when event not partial
      (doall (map #(dbpay/credit-payment ename edate % party-pay) users)))))

(defn participants-count [ename date]
  (count (dbpay/get-participants ename date)))

(defn- ^:deprecated extract-event [m]
  "Extract event keys from raw result of query participated-list."
  (select-keys m '(:name :price :date :author)))

(defn- ^:deprecated grouper [events]
  "Reorganize participation result to map, where key - is event, and value - vector of users, that 
  participate this event. Expect input, after using group-by on BD-table 'participants':
  (event-name, event-price, date, remain, user). Each row, can contains same event, with different users"
  (map (fn [[k v]] {:event k :users (mapv :user v)}) ;this func map usernames in vector
       (group-by extract-event events)))
