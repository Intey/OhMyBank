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

(defn add-user [uname birthdate rate]
  (sql/insert users (sql/values {:name uname :bdate birthdate :rate rate})))

(defn add-event [name price & date]
  (if-not (nil? date)
    (sql/insert events (sql/values {:name name :price price :date date}))  
    (sql/insert events (sql/values {:name name :price price }))))

(defn get-user [uname] (sql/select users (sql/where (= :name uname))))
(defn get-event [ename] (sql/select events (sql/where (= :name ename))))
(defn get-user-list   [] (sql/select users))
(defn get-events-list [] (sql/select events))

(defn get-user-events [uname]
  (sql/select participants (sql/fields)
              (sql/with users (sql/where (= :name uname)) (sql/fields))
              (sql/with events)))

