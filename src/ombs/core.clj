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
  (select-keys m '(:event :remain :price :date :debt)) )

(defn event-users []
  "Reorganize participation result to map, where key - is event, and value - vector of users, that 
  participate this event. Expect input, after using group-by on BD-table 'participants':
  (event-name, event-price, date, remain, user). Each row, can contains same event, with different users"
  (map
  (fn [[k v]]
    {:event k :users (mapv :username v)}) ;this func map usernames in vector
    (group-by extract-event (db/participated-list) ) ) )

(defn need-button? [uname event-users-pair]
 (->> event-users-pair
      :users
      (some #{uname}) boolean not))
  ;(apply
  ;(fn [[k v]] (nil? (some #{uname} v ))) ; is user in participate list?
  ;event-users-pair) )

(defn debt [username]
  ;(-> (db/get-user-events username) 
  ;  )
  )
   

