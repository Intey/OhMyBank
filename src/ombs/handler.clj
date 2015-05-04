(ns ombs.handler
  (:require
    [ombs.view :as view]
    [ombs.db :as db]
    [ombs.core :as core]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
))

(defn index [ctxt]
  "Handler. show index page"
  (view/index ctxt))

(defn regpage [_] (view/register {}))

(defn log-user [uname]
  "Generate user page, with his name and events."
  (sess/put! uname)
  (redirect "/")  
  )

(defn register [params]
  (if (core/reg-ok? (params "username") (params "pass1") (params "pass2"))  
    ;true
    (do 
      (db/add-user (params "username") (params "pass1") (params "birthdate") (core/rate (params "student-flag")))
      (log-user (params "username")) )
    ;fasle 
    (view/register params) 
    ))


(defn login [{{uname :username pass :password} :params}]
  (if  (= pass (:password (db/get-user uname)))
    (log-user uname)
    (view/index {:error "wrong login/pass"})
    )
  )

