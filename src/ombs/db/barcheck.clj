(ns ombs.db.barcheck
  (:require
    [ombs.db.old :refer :all]
    [korma.core :as sql]
    ))

(defn get-barcheck [date]
  "Return barcheck, where  "
  ; (sql/select barchecks (sql/where {:date date})
  ;             (sql/with baritems)
  ;             (sql/with participants)
  ;            ))
