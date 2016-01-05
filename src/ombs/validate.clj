(ns ombs.validate
  (:require
    [noir.validation :as vld]
    [ombs.db.old :as db]
    [ombs.db.payment :as dbp]
    [noir.session :as sess]
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
           :unexist         "Event %s does not exist"
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
          :unexist "Username %s doesn't exists"
          :low-balance "User %s doesn't have enough money"
          }
   :login {
           :invalid "Incorrect Login or password"
           }
   :fee {
         :unexist "Unexists fee"
         }
   :pay {
         :wrong-parts "Parts count %s is greater that we have."
         :wrong-money "Money should be > 0"
         }

   }
  )

(defn- message
  "Giving message text form mesage-map, using tags. If spend some `data` to
  this, format string if possible. Can spend only 0-1 data-string."
  ([tags] (message tags ""))
  ([ tags & data ]
   (apply format (get-in errors tags "Internal: Wrong message link") data)) )

(defn errors-string
  ([] (reduce str (map #(str "|" % "|") (vld/get-errors))))
  ([tags] (reduce str (map #(str "|" % "|") (vld/get-errors tags)))))

(defn errors? [] (vld/errors?))

(defn add-error [tag text] (vld/set-error! tag text))

(defmacro create-rule [tag [validator msg]]
  `(vld/rule ~@(list validator) [~tag ~msg] )
  )

(defmacro create-validator
  "Create validation block, start with clearing errors, followed by create-rule
  calls for each ve-pairs.  ve-pairs is validator-error-pairs(vector) where
  first is function(rule that returns boolean), and second is error message,
  that was appends in vld errors vector, when rule fails."
  [tag ve-pairs]
  `(do
     ;(vld/clear-errors!)
     ~@(map #(list 'create-rule tag %) ve-pairs)
     (not (vld/errors? ~tag)))
  )

(defn rule [[validator msg]]
   (if-not validator
     msg
     nil))


(cond-> {}
  false (msg-insert :e "First")
  false (msg-insert :e "Second")
  true (msg-insert :f "Third")
  false (msg-insert :d "ERoror")
  true (msg-insert :f (message [:event :empty-name]))
  )



(defn msg-insert [coll tag msg]
  (update coll tag #(vec (conj % msg)))
  )

(defn new-event? [eventname price date]
  (cond-> {}
    (vld/has-value? eventname)              (msg-insert :event (message [:event :empty-name]))
    (vld/greater-than? price 0)             (msg-insert :event (message [:event :zero-price]))
    (vld/has-value? date)                   (msg-insert :event (message [:event :empty-date]))
    (empty? (db/get-event eventname date))  (msg-insert :event (message [:event :duplicate-event]))))

(defn new-user? [username pass1 pass2]
  (cond-> {}
    (vld/has-value? username)       (msg-insert :login (message [:user :empty-name]))
    (>= (count pass1) 8)            (msg-insert :login (message [:register :short-pass]))
    (= pass1 pass2)                 (msg-insert :login (message [:register :notmatch-pass]))
    (empty? (db/get-user username)) (msg-insert :login (message [:user :unexist]))
    ))

(defn login? [username password]
  (cond-> {}
    (vld/has-value? username)                       (msg-insert :login (message [:user :empty-name]))
    (vld/has-value? (db/get-user username))         (msg-insert :login (message [:user :invalid]))
    (= password (:password (db/get-user username))) (msg-insert :login (message [:login :invalid]))
    ))

(defn participation? [eid]
  (cond-> {}
    (not= (db/get-status eid) (db/statuses :finished)) (msg-insert :participation (message [:event :finished]))
    (not= "" (:name (db/get-event eid)))               (msg-insert :participation (message [:event :unexist]))
                     ))

(defn payment? [eid uid parts]
  "Check, if id's is correct. Used with (db/get-*id)"
  (create-validator :pay
                    [
                     [(not= nil uid)
                      (message [:user :empty-name])]
                     [(not= nil eid)
                      (message [:event :unexist])]
                     [(<= parts (+ (dbp/free-parts eid) parts))
                      (message [:pay :wrong-parts])]
                     ]))
(defn fee? [id]
 (create-validator :pay
                   [
                    [(not= nil id)
                     (message [:fee :unexist])]
                    [(not (nil? (db/event-from-fee id)))
                     (message [:event :unexist])]
                    [(vld/has-value? (sess/get :username))
                     (message [:user :unexist])]
                    ]))

(defn parts? [ename date parts]
  (create-validator :pay
                    [ ; FIXME: free-parts include parts from currect fee
                     [(<= parts (+ (dbp/free-parts ename date) parts))
                      (message [:event :parts-count])]
                     ]))

(defn moneyout? [username money]
  (create-validator :admin
                    [
                     [(nil? (db/get-user username)) (message [:user :unexist])]
                     [(< 0 money) (message [:pay :wrong-money])]
                     [(<= money (:balance (db/get-user username))) (message [:user :low-balance] username)]
                     ]
                    )
  )

