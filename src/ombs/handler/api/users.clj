(ns ombs.handler.api.users
  (:require
    [schema.core :as s]
    [ombs.db.user :as db]
    ))

(s/defschema User
  {:login (s/pred db/exists? "Valid account")}
  )
