(ns ombs.handler.api.users
  (:require
    [schema.core :as s]
    [ombs.db.user :as db]
    [compojure.api.sweet :refer [describe]]
    ))

(s/defschema User
  {:login  (describe (s/pred db/exists? "Valid user") "Registred username" :type "String")}
  )

(defn add-user [user]
  (println user)
  user
  )

(defn exists? [username] (db/exists? username))
