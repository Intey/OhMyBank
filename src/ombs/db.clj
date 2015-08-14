(ns ombs.db
  (:require [korma.db :as kdb]
            [korma.core :as sql]))

(kdb/defdb korma-db (kdb/sqlite3
                      { :db "database.db"
                       :user "user"
                       :password "placeholder"}))
; about many-to-many https://groups.google.com/d/msg/sqlkorma/r3kR6DyQZHo/RrQS_J8kkQ8J

(declare events)

(sql/defentity users
  (sql/many-to-many events :pays {:lfk :uid :rfk :eid}))

(sql/defentity events
  (sql/many-to-many users :pays {:lfk :eid :rfk :uid}))

(sql/defentity pays
  (sql/belongs-to events {:fk :eid})
  (sql/belongs-to users {:fk :uid})
  ) 

(sql/defentity summary)
(sql/defentity debts)

(sql/defentity participation)
(sql/defentity participants)

; statuses - describe status of event. 
;   Initial - created, but not started. Collecting participants.
;   In-progress - Collecting money! Not full sum payed.
;   Finished - closed.
(def statuses {:initial "initial" :finished "finished" :in-progress "in-progress"})

; ===========================================================================================================
; ===========================================================================================================

(defn add-user [uname password birthdate rate]
  (sql/insert users (sql/values {:name uname
                                 :password password
                                 :bdate birthdate
                                 :rate rate } )))

(defn add-event 
  ([ename price author date] 
   (sql/insert events (sql/values {:name ename :price price :author author :date date :status (statuses :initial)})))
  ([ename price author] 
   (sql/insert events (sql/values {:name ename :price price :author author :status (statuses :initial)}))))

(defn get-user [uname]
  "Return map of user info"
  (first (sql/select users
           (sql/fields :name :bdate :balance :rate :password)
           (sql/where (= :name uname))
           (sql/limit 1)
           )))

(defn get-event [ename date] (first (sql/select events (sql/where (and (= :date date) (= :name ename))))))

(defn get-events [] (sql/select events))

(defn get-active-events [] (sql/select events (sql/where (not= :status (statuses :finished)))))

(defn get-uid [uname]
  (:id (first (sql/select users (sql/fields :id)
                          (sql/where (= :name uname))))))

(defn get-eid [ename date]
  (:id (first (sql/select events (sql/fields :id)
                          (sql/where (and (= :name ename) (= :date date)))))))

(defn participated? 
 "Check participation in event. If user have some payment action on event - he is participated." 
  ([uid eid] (not (empty? (sql/select participation (sql/where (and (= :uid uid) (= :eid eid))))))) 
  ([uname ename edate] (participated? (get-uid uname) (get-eid ename edate)))
  )

(defn get-usernames [] (sql/select users (sql/fields :name)))

(defn get-rate [uname] (:rate (first (sql/select users (sql/fields :rate) (sql/where (= :name uname))))))

(defn get-rates [usernames] (map #(get-rate %) usernames))

(defn get-debt 
  "sum of all credits and debits in pays for current user and event. "
  ; full debt
  ([username]
  (if-let [summary (:debt (first (sql/select debts (sql/where (= username :user)) (sql/aggregate (sum :debt) :debt))))]
    summary 
    0.0))

  ; debt on some event
  ([username event date]
  (if-let [summary (:debt (first (sql/select debts (sql/where (and (= username :user) (= event :event) (= date :date) )))))]
    summary 
    0.0))
  ) 

(defn credit-payment [eventname date username money] 
  (println (str "credit " eventname " date " date " user " username " money " money ))
  (sql/insert pays (sql/values { :uid (get-uid username) :eid (get-eid eventname date) :credit money })))

(defn debit-payment [uid eid money]
  (sql/insert pays (sql/values { :uid uid :eid eid :debit money }))
  )

(defn add-participant [event date user]
  (sql/insert participation (sql/values {:eid (get-eid event date) :uid (get-uid user)}))
  )

(defn is-initial? [ename date] 
   (= (statuses :initial) 
      (:status (first (sql/select events (sql/where {:name ename :date date} ) (sql/fields :status))))))

(defn set-status [ename date s]

  (println (str "set status " (statuses s) " to evnt " ename " date " date  ))
  (sql/update events (sql/set-fields {:status (statuses s)}) 
              (sql/where {:name ename :date date})) )

(defn get-status [ename date]
  (:status (first (sql/select events (sql/where {:name ename :date date} ) (sql/fields :status)))))

(defn get-participants [ename edate]
  (mapv #(first (vals %)) (sql/select participants (sql/fields :user) (sql/where {:event ename :date edate}))))
