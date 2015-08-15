(ns ombs.validate
  (:require
    [noir.validation :as vld]
    [ombs.db :as db]
    [clojure.pprint :refer [pprint]]
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
  ([tags] (reduce str (map #(str "|" % "|") (let [errs (vld/get-errors tags)]
                                              (println (str "errs: " errs))
                                              errs
                                              )))))

(defmacro create-rule [tag data]
  `(vld/rule ~@(list (first data)) [~tag ~(last data)] )
  )

(defmacro create-validator
  "Create validation block, start with clearing errors, followed by create-rule calls for each ve-pairs.
  ve-pairs is validator-error-pairs(vector) where first is function(rule that returns boolean), and second is error message, that was appends
  in vld errors vector, when rule fails."
  [tag ve-pairs]
  `(do
     (vld/clear-errors!)
     ~@(map #(macroexpand (list 'create-rule tag %)) ve-pairs)
     (not (vld/errors? ~tag)))
  )

(defn add-error [tag text] (vld/set-error! tag text))

(defn new-event? [eventname price date]
  (create-validator :event 
                    [
                     [ (vld/has-value? eventname)              (get-in errors [:event :empty-name])      ]
                     [ (vld/greater-than? price 0)             (get-in errors [:event :zero-price])      ]
                     [ (vld/has-value? date)                   (get-in errors [:event :empty-date])      ]
                     [ (empty? (db/get-event eventname date))  (get-in errors [:event :duplicate-event]) ]
                     ]))

(defn new-user? [username pass1 pass2]
  (create-validator :register 
                    [
                     [(vld/has-value? username) "Username can't be empty"]
                     [(>= (count pass1) 8) "Password should be longer than 7 chars"]  
                     [(= pass1 pass2) "Password doesn't match"]                        
                     [(empty? (db/get-user username)) "Username already used!" ] 
                     ]))

(defn login? [username password]
  (create-validator :login 
                    [[ (vld/has-value? username)                       "Username can't be empty" ]
                     [ (vld/has-value? (:name (db/get-user username))) "User not found"]
                     [ (= password (:password (db/get-user username))) "Incorrect Login or password"]
                     ]))

(defn ids? [eid uid]
  "Check, if id's is correct. Used with (db/get-*id)"
  (create-validator :pay 
                    [
                     [(nil? uid) "User not exists."]
                     [(nil? eid) "Event does not exist!" ]     
                     ]))

(defn participation? [ename date uname]
  (create-validator :participation 
                    [
                     [(vld/has-value? ename) "Event name is empty"]                                        
                     [(vld/has-value? date) "Event date is empty"]                                        
                     [(vld/has-value? uname) "Username is empty"]                                         
                     [(not= (db/get-status ename date) (db/statuses :finished)) "Event already finished"]  
                     ]))
