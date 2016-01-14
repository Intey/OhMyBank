(ns ombs.db.old
  (:require [korma.db :as kdb]
            [korma.core :as sql]
            [ombs.funcs :as f]
            ))

; about many-to-many https://groups.google.com/d/msg/sqlkorma/r3kR6DyQZHo/RrQS_J8kkQ8J

(declare events)
(declare goods)
(declare fees)

(defrecord User [name password bdate role])
(defrecord Event [name date price author status type parts])

(sql/defentity users
  (sql/many-to-many events :pays {:lfk :users_id :rfk :events_id})
  (sql/transform #(User. (:name %) (:password %) (:bdate %) (:role %)))
  (sql/has-many fees)
  )

(sql/defentity events
  (sql/many-to-many users :pays {:lfk :events_id :rfk :users_id})
  (sql/has-one goods)
  (sql/has-one fees)
  )

(sql/defentity pays
  (sql/belongs-to events {:fk :events_id})
  (sql/belongs-to users {:fk :users_id}))

(sql/defentity fees
  (sql/belongs-to events)
  (sql/belongs-to users))


(sql/defentity goods
  (sql/has-many events {:fk :events_id}))

; DATA VIEWS
(sql/defentity summary)
(sql/defentity debts)
(sql/defentity balances)
;FIXME: delete one
(sql/defentity participation)
(sql/defentity participants)

; statuses - describe status of event.
;   Initial - created, but not started. Collecting participants.
;   In-progress - Collecting money! Not full sum payed.
;   Finished - closed.
(def admin-role-value 0)
; money out 'event id'
