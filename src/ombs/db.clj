(ns ombs.db
  (:require [korma.db :as kdb]
            [korma.core :as sql]))

(kdb/defdb korma-db (kdb/sqlite3
                      { :db "database.db"
                       :user "user"
                       :password "placeholder"}))
; about many-to-many https://groups.google.com/d/msg/sqlkorma/r3kR6DyQZHo/RrQS_J8kkQ8J

(declare events)
(sql/defentity 
  users
  (sql/many-to-many events :participants {:lfk :uid :rfk :eid}))

(sql/defentity 
  events
  (sql/many-to-many users :participants {:lfk :eid :rfk :uid}))

(sql/defentity 
  participants
  (sql/belongs-to events {:fk :eid})
  (sql/belongs-to users {:fk :uid}))

(defn add-user [uname password birthdate rate]
  (sql/insert users (sql/values {:name uname 
                                 :password password 
                                 :bdate birthdate 
                                 :rate rate } )))

(defn add-event [name price & date]
  (if-not (nil? date)
    (sql/insert events (sql/values {:name name :price price :date date}))
    (sql/insert events (sql/values {:name name :price price }))))

(defn remove-user [name]
  (sql/delete users (sql/where (= :name name))))

(defn get-user [uname]
  "Return map of user info"
  (first (sql/select users
              (sql/fields :name :bdate :balance :rate :password)
              (sql/where (= :name uname))
              (sql/limit 1)
              )))

(defn get-event [ename] 
  "Return fields of event"
  (sql/select events 
              (sql/fields :name :price :remain)
              (sql/where (= :name ename))))

(defn get-user-list   []
  "Return list of users"
  (sql/fields :name :bdate :balance :rate :password)
  (sql/select users))

(defn get-events-list [] 
  "Return map of events"
  (sql/select events))

(defn get-user-events [uname]
  (sql/select participants (sql/fields)
              (sql/with users (sql/where (= :name uname)) (sql/fields))
              (sql/with events)))
