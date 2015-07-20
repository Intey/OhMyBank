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
  (sql/many-to-many events :participants {:lfk :uid :rfk :eid}))

(sql/defentity events
  (sql/many-to-many users :participants {:lfk :eid :rfk :uid}))

(sql/defentity participants
  (sql/belongs-to events {:fk :eid})
  (sql/belongs-to users {:fk :uid}))

(sql/defentity pays
  (sql/belongs-to events {:fk :eid})
  (sql/belongs-to users {:fk :uid})
  ) 
   

(sql/defentity participation)

(sql/defentity groupedParticipants)

(defn add-user [uname password birthdate rate]
  (sql/insert users (sql/values {:name uname
                                 :password password
                                 :bdate birthdate
                                 :rate rate } )))

(defn add-event [ename price & [date]]
  (if-not (nil? date)
    (sql/insert events (sql/values {:name ename :price price :remain price :date date}))
    (sql/insert events (sql/values {:name ename :price price :remain price }))))

(defn remove-user [ename]
  (sql/delete users (sql/where (= :name ename))))

(defn get-user [uname]
  "Return map of user info"
  (first (sql/select users
                     (sql/fields :name :bdate :balance :rate :password)
                     (sql/where (= :name uname))
                     (sql/limit 1)
                     )))

(defn get-event [ename date] 
  "Return fields of event"
  (sql/select events
              (sql/where (and (= :date date) (= :name ename)))))

(defn get-events-list []
  "Return map of events"
  (sql/select events))

(defn get-uid [uname]
  (:id (first (sql/select users (sql/fields :id)
              (sql/where (= :name uname))))))

(defn get-eid [ename date]
  (:id (first (sql/select events (sql/fields :id)
                          (sql/where (and (= :name ename) (= :date date)))))))


(defn participapated? [uid eid]
  (not (empty? (sql/select participants (sql/where
                                          (and (= :uid uid)
                                               (= :eid eid)))))))

(defn event-price [id]
  (:price (first (sql/select events (sql/where (= :id id)) (sql/fields [:price]))))
  )

(defn add-participate [uid eid]
  (println (str uid ":" eid))
  (if-not ( participapated? uid eid) 
    (do 
      (sql/insert participants (sql/values {:uid uid :eid eid}))
      (sql/insert pays (sql/values {:uid uid :eid eid :credit (event-price eid)}))
      )
    nil
    ) 
)

(defn get-user-events [uname]
  (sql/select participants (sql/fields)
              (sql/with users (sql/where (= :name uname)) (sql/fields))
              (sql/with events (sql/fields [:name :event]))))

(defn participated-list []
  ;need grouping by event params. Should get event params and vector of it's users.
  ;Actually it's solved by sqlite.
  (sql/select participation) )

(defn get-usernames []
  "Return list of users names"
  (sql/select users
    (sql/fields :name))
  )

(defn get-users   [] "Return list of users"
  (sql/select users
              (sql/fields :name :bdate :balance :rate :password) 
              ))

(defn credit-payment [uid eid money]
  (println (str "add credit : "uid" event: "eid " price: "money) )
  (sql/insert pays (sql/values { :uid uid :eid eid :credit money }))
  )
(defn debit-payment [uid eid money]
  (println (str "add debit: "uid" event: "eid " price: "money) )
  (sql/insert pays (sql/values { :uid uid :eid eid :credit money }))
  )

(defn get-rate [uname]
  (:rate (first (sql/select users (sql/fields :rate) 
                            (sql/where (= :name uname))))))

(defn get-rates [usernames]
  (map #(get-rate %) usernames)
  )

(defn get-debt [uid eid]
  ; sum of all credits and debits in pays for current user and event. 
  (:debt (first (sql/select pays 
              (sql/where (and (= :eid eid) (= :uid uid)))
              (sql/aggregate (sum :credit) :debt)
              )))
  )
