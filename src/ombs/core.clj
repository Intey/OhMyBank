(ns ombs.core
  "contains main logic"
  (:require [ombs.db :as db]
            [ombs.funcs :as fns]
            [noir.validation :as valids]
            ))

(defn reg-ok? [username pass1 pass2]
  "check user post params on errors, and return false if some is not pass"
  (if (and (not (empty? username)) (> (count pass1) 8) (= pass1 pass2) )
    true
    false
    )
  )

(defn rate [student?]
  "Return rate for user. student - 0.5; else - 1."
  (if (= "on" student?)
    0.5
    1.0 ) )

(defn extract-event [m]
  "Extract event keys from raw result of query participated-list."
  (select-keys m '(:event :price :date :author)))

(defn grouper [events]
  "Reorganize participation result to map, where key - is event, and value - vector of users, that 
  participate this event. Expect input, after using group-by on BD-table 'participants':
  (event-name, event-price, date, remain, user). Each row, can contains same event, with different users"
  (map (fn [[k v]] {:event k :users (mapv :user v)}) ;this func map usernames in vector
       (group-by extract-event events)))

(defn stakes [] (grouper (db/get-stakes)) )

(defn user-events [username] (grouper (db/get-events-created-by username)))

(defn need-button? [uname events]
 (->> events
      :users
      (some #{uname}) boolean not))
  ;(apply
  ;(fn [[k v]] (nil? (some #{uname} v ))) ; is user in participate list?
  ;event-users-pair) )

(defn debt 
  ([username] (db/get-debt username))
  ([username event date] (db/get-debt username event date))
  )

(defn as-vec
  "get a vector or value and represents it as vector. [1 2 3] -> [1 2 3]; 1 -> [1]"
  [x] (if-not (vector? x) 
        (conj [] x)
        x))

(defn party-pay [event-price users]
  (/ (read-string event-price) (count users)))

(defn is-initial? [ename date] (db/is-initial? ename date))
