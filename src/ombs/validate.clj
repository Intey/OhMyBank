(ns ombs.validate
  (:require
    [noir.validation :as vld]
    [ombs.dbold :as db]
    [noir.response :refer [redirect]]
    ))

(def errors
  {
   :event {
           :empty-name      "Event name should not be empty"
           :zero-price      "Event price should be greater than 0"
           :empty-date      "Event should have date"
           :duplicate-event "Event with same name today was created. Use another name"
           :no-participants "Participants should be checked"
           :unexist         "Event does not exist"
           :finished        "Event is history"
           :parts-count     "Event have less parts that given"
           }
   :register {
              :short-pass      "Password should be longer than 7 chars"
              :notmatch-pass   "Password doesn't match"
              }
   :user {
          :empty-name "Username can't be empty"
          :not-found "User not found"
          :unexists "Username already used"
          }
   :login {
           :invalid "Incorrect Login or password"
           }
   }
  )

(defn- message [ & tgs ] (get-in errors (vec tgs)))

(defn errors-string
  ([] (reduce str (map #(str "|" % "|") (vld/get-errors))))
  ([tags] (reduce str (map #(str "|" % "|") (vld/get-errors tags)))))

(defmacro create-rule [tag data]
  `(vld/rule ~@(list (first data)) [~tag ~(last data)] )
  )

(defmacro create-validator
  "Create validation block, start with clearing errors, followed by create-rule calls for each ve-pairs.
  ve-pairs is validator-error-pairs(vector) where first is function(rule that returns boolean), and second is error message, that was appends
  in vld errors vector, when rule fails."
  [tag ve-pairs]
  `(do
     ;(vld/clear-errors!)
     ~@(map #(list 'create-rule tag %) ve-pairs)
     (not (vld/errors? ~tag)))
  )

(defn add-error [tag text] (vld/set-error! tag text))

(defn new-event? [eventname price date]
  (create-validator :event
                    [
                     [ (vld/has-value? eventname)              (message :event :empty-name)      ]
                     [ (vld/greater-than? price 0)             (message :event :zero-price)      ]
                     [ (vld/has-value? date)                   (message :event :empty-date)      ]
                     [ (empty? (db/get-event eventname date))  (message :event :duplicate-event) ]
                     ]))

(defn new-user? [username pass1 pass2]
  (create-validator :register
                    [
                     [(vld/has-value? username) (message :user :empty-name)]
                     [(>= (count pass1) 8) (message :register :short-pass)]
                     [(= pass1 pass2) (message :register :notmatch-pass)]
                     [(empty? (db/get-user username)) (message :user :exists) ]
                     ]))

(defn login? [username password]
  (create-validator :login
                    [[ (vld/has-value? username) (message :user :empty-name)]
                     [ (vld/has-value? (db/get-user username)) (message :user :not-found)]
                     [ (= password (:password (db/get-user username))) (message :login :invalid)]
                     ])
  )

(defn participation? [ename date uname]
  (create-validator :participation
                    [
                     [(not= (db/get-status ename date) (db/statuses :finished)) (message :event :finished)]
                     [(vld/has-value? ename) (message :event :empty-name)]
                     [(vld/has-value? date) (message :event :empty-date)]
                     [(vld/has-value? uname) (message :user :empty-name)]
                     ]))

(defn ids? [eid uid]
  "Check, if id's is correct. Used with (db/get-*id)"
  (create-validator :pay
                    [
                     [(not= nil uid) (message :user :empty-name)]
                     [(not= nil eid) (message :event :unexist)]
                     ])
  )

(defn parts? [ename date parts]
  (create-validator :pay
                    [
                     [(<= parts (db/get-rest-parts ename date)) (message :event :parts-count)]
                     ])
  )
