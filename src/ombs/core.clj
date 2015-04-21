(ns ombs.core
  "contains main logic"
  (:require
    [ombs.db :as db]))

(defn check-acc [username pass]
  "search account in database and login user. Needs session, load data, etc."
  true
  ;(= pass (:password (db/get-user username)))
  )

(defn reg-acc [username pass birthdate rate]
  (if (empty? (db/get-user username))
    (db/add-user username pass)
    ;show error
    ))
