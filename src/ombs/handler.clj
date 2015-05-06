(ns ombs.handler
  (:require
    [ombs.view :as view]
    [ombs.db :as db]
    [ombs.core :as core]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
))

(defn index [& ctxt]
  "Handler. show index page"
  (view/index ctxt))

(defn regpage [_] (view/register {}))

(defn log-user [uname]
  "Generate user page, with his name and events."
  (sess/put! :username uname)
  (redirect "/user")
  )

(defn register [params]
  (if (core/reg-ok? (:username params) (:password1 params) (:password2 params))
    ;true
    (do 
      (db/add-user (:username params) (:password1 params) (:birthdate params) (core/rate (:student-flag params)))
      (log-user (:username params)) )
    ;fasle 
    (view/register params) 
    ))


(defn login [ { {uname :username pass :password :as params} :params} ]
  (if (= pass (:password (db/get-user uname)))
    (log-user uname)
    (view/index {:error "wrong login/pass"})
    )
  )

(defn logout [& _] 
  (sess/clear!)
  (redirect "/")
  )

( defn user [& _]
  (view/user)
  )
