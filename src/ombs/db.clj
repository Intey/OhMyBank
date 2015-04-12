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

(sql/select participants
            (sql/fields ) ;don't select fields from participants
            (sql/with events (sql/fields [:name :event]))
            (sql/with users  (sql/fields [:name :user]))
            (sql/group :user))


