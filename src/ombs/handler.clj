(ns ombs.handler
  (:require
    [ombs.view :as view]
    [ombs.db :as db]
    [ombs.core :as core]
    [noir.response :refer [redirect] ]
))

(defn index [ctxt]
  "Handler. show index page"
  (view/index ctxt))

(defn user [params]
  "Generate user page, with his name and events."
  (view/user (:username params))
  )

(defn regpage [_] (view/register {}))

(defn register [params]
  (if (core/reg-ok? (params "username") (params "pass1") (params "pass2"))  
    (do    
      (db/add-user (params "username") (params "pass1") (params "birthdate") (core/rate (params "student-flag")))
      (redirect "/") )
    (view/register params) 
    ))

