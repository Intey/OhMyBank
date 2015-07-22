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

(sql/defentity stakes)
(sql/defentity summary)
(sql/defentity debts)


; ===========================================================================================================
; ===========================================================================================================

(defn add-user [uname password birthdate rate]
  (sql/insert users (sql/values {:name uname
                                 :password password
                                 :bdate birthdate
                                 :rate rate } )))

(defn add-event [ename price & [date]]
  (if-not (nil? date)
    (sql/insert events (sql/values {:name ename :price price :remain price :date date}))
    (sql/insert events (sql/values {:name ename :price price :remain price }))))

(defn get-user [uname]
  "Return map of user info"
  (first (sql/select users
           (sql/fields :name :bdate :balance :rate :password)
           (sql/where (= :name uname))
           (sql/limit 1)
           )))

(defn get-event [ename date] (sql/select events (sql/where (and (= :date date) (= :name ename)))))

(defn get-events-list [] (sql/select events))

(defn get-uid [uname]
  (:id (first (sql/select users (sql/fields :id)
                          (sql/where (= :name uname))))))

(defn get-eid [ename date]
  (:id (first (sql/select events (sql/fields :id)
                          (sql/where (and (= :name ename) (= :date date)))))))

(defn participated? [uid eid]
  "Check participation in event. If user have some payment action on event - he is participated."
  (not (empty? (sql/select pays (sql/where (and (= :uid uid) (= :eid eid))))))) 

(defn event-price [id] (:price (first (sql/select events (sql/where (= :id id)) (sql/fields [:price])))))

(defn get-stakes [] (sql/select stakes) )

(defn get-usernames [] (sql/select users (sql/fields :name)))

(defn get-rate [uname] (:rate (first (sql/select users (sql/fields :rate) (sql/where (= :name uname))))))

(defn get-rates [usernames] (map #(get-rate %) usernames))

(defn get-credit [uid eid]
  ; sum of all credits and debits in pays for current user and event. 
  (if-let [summary 
           (:credit (first (sql/select pays (sql/where (and (= :eid eid) (= :uid uid))) 
                              (sql/aggregate (sum :credit) :credit))))]
    summary
    0.0 ; if value - nil, return 0
    )
  )

(defn get-debt [username event date]
  "sum of all credits and debits in pays for current user and event. "
  (if-let [summary (:debt (first (sql/select debts (sql/where (and (= username :user) (= event :event) (= date :date) )))))]
    summary
    0.0 ; if value - nil, return 0.0
    )
  ) 

(defn credit-payment [uid eid money]
  (println (str "add credit : "uid" event: "eid " price: "money) )
  (sql/insert pays (sql/values { :uid uid :eid eid :credit money }))
  )
(defn debit-payment [uid eid money]
  (println (str "add debit: "uid" event: "eid " price: "money) )
  (sql/insert pays (sql/values { :uid uid :eid eid :debit money }))
  )
