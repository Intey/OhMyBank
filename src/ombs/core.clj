(ns ombs.core 
  (:require 
    [ombs.views :as views]
    [ombs.db :as db]))

(defn index [& error]
  "Handler. show index page"
  (views/page-index {:error error})) 

(defn reg-acc [username pass birthdate rate] 
  (if (empty? (db/get-user username))
    (db/add-user username pass)
    ;show error
    ))

(defn check-acc [username pass]
  "search account in database and login user. Needs session, load data, etc."
  true
  ;(= pass (:password (db/get-user username)))
  )
