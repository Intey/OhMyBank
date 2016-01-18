(ns ombs.handler.api.users
  (:require
    [schema.core :as s]
    [ombs.db.user :as db]
    ))

(s/defschema User
  {:login  (describe (s/pred db/exists? "Invalid user") "Registred username" :type "String")}
  )

(defn add-user [user]
  (println user)
  user
  )
