(ns ombs.validate 
  (:require 
    [noir.validation :as vld]
    [ombs.db :as db]
    ))

(def errors 
  {
   :event { 
            :empty-name      "Event name should not be empty"  
            :zero-price      "Event price should be greater than 0"
            :empty-date      "Event should have date"                                  
            :duplicate-event "Event with same name today was created. Use another name"
            :no-participants "Participants should be checked"                          
            }
   }
  )

(defn errors-string 
  ([] (reduce str (map #(str "|" % "|") (vld/get-errors))))
  ([tags] (reduce str (map #(str "|" % "|") (vld/get-errors tags))))
  )

(defmacro create-rule [tag data]
  `(vld/rule (first ~data) [~tag (last ~data)])
  )

;(defmacro create-validator [tag rules]
;  (vld/clear-errors!)
;  (map #(create-rule tag %) rules)
;  (not (vld/errors? tag))
;  )


(defn registration? [params]
  (vld/clear-errors!)
  ; there your code !
  (not (vld/errors? :ename :eprice :edate :event))) 

(defn new-event? [eventname price date users] 
  (vld/clear-errors!)
  (create-rule :event [ (vld/has-value? eventname)              (get-in errors [:event :empty-name]) ])
  (create-rule :event [ (vld/greater-than? price 0)             (get-in errors [:event :zero-price]) ])
  (create-rule :event [ (vld/has-value? date)                   (get-in errors [:event :empty-date]) ])
  (create-rule :event [ (empty? (db/get-event eventname date))  (get-in errors [:event :duplicate-event]) ])
  (create-rule :event [ (not (nil? users))                      (get-in errors [:event :no-participants]) ])
  (not (vld/errors? :event)))

(defn new-user? [username pass1 pass2] 
  (vld/clear-errors!)
  (vld/rule (vld/has-value? username) [:register "Username can't be empty"])
  (vld/rule (>= (count pass1) 8) [:register "Password should be longer than 7 chars"])
  (vld/rule (= pass1 pass2) [:register "Password doesn't match"])
  (create-rule :register [ (empty? (db/get-user username)) "Username already used!" ])
  (not (vld/errors? :register))
  )

(defn login? [username password] 
  ;(create-validator :login [[ (vld/has-value? username)                       "Username can't be empty" ]
  ;                          [ (vld/has-value? (:name (db/get-user username))) "User not found"]
  ;                          [ (= password (:password (db/get-user username))) "Incorrect Login or password"]
  ;                          ])
  (vld/clear-errors!)
  (vld/rule (vld/has-value? username) [:login "Username can't be empty"])
  (vld/rule (vld/has-value? (:name (db/get-user username))) [:login "User not found"])
  (vld/rule (= password (:password (db/get-user username))) [:login "Incorrect Login or password"])
  (not (vld/errors? :login))
  
  )

(defn stake? [eid uid]
;  (create-validator :pay [[ (vld/has-value? uid) "User not found in database" ]
;                          [ (vld/has-value? eid) "Event not found in database"] ])
;
  (vld/clear-errors!)
  (create-rule :pay [ (vld/has-value? eid) "Event does not exist!" ])
  (create-rule :pay [ (vld/has-value? uid) "User not exists!" ])
  (not (vld/errors? :pay)))
