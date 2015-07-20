(ns ombs.validate 
  (:require 
    [noir.validation :as vld]
    [ombs.db :as db]
    ))

(defn errors-string [tags]
  (reduce str (map #(str "|" % "|") (vld/get-errors tags)))
  )

(defn registration? [params]
  (vld/clear-errors!)
  ; there your code !
  (not (vld/errors? :ename :eprice :edate :event))) 

(defn new-event? [eventname price date users] 
  (vld/clear-errors!)
  (vld/rule (vld/has-value? eventname) [:event "Event name should not be empty"])
  (vld/rule (vld/greater-than? price 0) [:event "Event price should be greater than 0"])
  (vld/rule (vld/has-value? date) [:event "Event should have date"])
  (vld/rule (empty? (db/get-event eventname date)) [:event "Event with same name today was created. Use another name"])
  (vld/rule (not (nil? users)) [:event "Participants should be checked"])
  (not (vld/errors? :event)))

(defn new-user? [username pass1 pass2] 
  (vld/clear-errors!)
  (vld/rule (vld/has-value? username) [:register "Username can't be empty"])
  (vld/rule (>= (count pass1) 8) [:register "Password should be longer than 7 chars"])
  (vld/rule (= pass1 pass2) [:register "Password doesn't match"])
  (not (vld/errors? :register))
  )

(defn login? [username password] 
  (vld/rule (vld/has-value? username) [:login "Username can't be empty"])
  (vld/rule (vld/has-value? (:name (db/get-user username))) [:login "User not found"])
  (vld/rule (= password (:password (db/get-user username))) [:login "Incorrect Login or password"])
  (not (vld/errors? :login))
  
  )
