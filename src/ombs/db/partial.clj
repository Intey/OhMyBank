(ns ombs.db.partial
  (:require
    [korma.db :as kdb]
    [korma.core :as sql]
    [ombs.db.old :refer :all] ; for entity
    [ombs.db.event :as dbe]
    [ombs.funcs :as f]
            ))

;2 transacts
(defn add-goods [ename date parts]
  (sql/insert goods (sql/values {:events_id (dbe/get-eid ename date) :rest parts})))

;2 transacts
(defn rest-parts
  "Return parts, that "
  ([ename date]
   (:rest (first (sql/select goods (sql/fields :rest)
                             (sql/where {:events_id (dbe/get-eid ename date)})))))

  ([eid]
   (:rest (first (sql/select goods (sql/fields :rest)
                             (sql/where {:events_id eid}))))))

;3 transacts
(defn shrink-goods
  ([ename date parts]
   "Sub count parst from database"
   (sql/update goods (sql/set-fields {:rest (- (dbe/get-parts ename date) parts)})
               (sql/where {:events_id (dbe/get-eid ename date)})))
  ([eid parts]
   (sql/update goods (sql/set-fields {:rest (- (dbe/get-parts eid) parts)})
               (sql/where {:events_id eid})))

  )

(defn parts-price [eid parts]
  (* parts (f/part-price (dbe/get-price eid) (dbe/get-parts eid))))

