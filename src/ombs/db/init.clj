(ns ombs.db.init (:require [korma.db :as kdb]))

(defn database []
  (kdb/defdb korma-db (kdb/sqlite3
                      { :db "database.db"
                       :user "user"
                       :password "placeholder"})))
