(ns ombs.ddm
  "contains main logic"
  (:require [ombs.db :as db]
            [noir.validation :as valids]
            [noir.session :as sess]))

(defn reg-ok? [username pass1 pass2]
  "check user post params on errors, and return false if some is not pass"
  (if (and (not (empty? username)) (> (count pass1) 8) (= pass1 pass2) )
    true
    false
    )
  )

(defn user-debt []
  (let [username (sess/get :username) ] 
    (str 0)
    )
  )

(defn rate [student?]
  "Return rate for user. student - 0.5; else - 1."
  (if (= "on" student?)
    0.5
    1.0 ) )

(defn extract-event [m]
  "Extract event keys from raw result of query participated-list."
  (select-keys m '(:event :remain :price :date)) )

(defn event-users []
  "Reorganize participation result to map, where key - is event,
  and value - vector of users, that participate this event."
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

