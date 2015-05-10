(ns ombs.handler
  (:require
    [ombs.view :as view]
    [ombs.db :as db]
    [ombs.core :as core]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [noir.validation :as vld]
    ))

(defn index [& [params]]
  "Handler. show index page with events."
  (view/index (assoc params :events (db/get-events-list)))
  )

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
  (view/user (db/get-user-events (sess/get :username)))
  )

(defn add-event [{{ename :name price :price date :date :as params} :params}]
  (str params)
  (vld/clear-errors!)
  (vld/rule (vld/has-value? ename) [:ename "Event name should not b empty"])
  (vld/rule (vld/greater-than? price 0) [:eprice "Event price should be greater than 0"])  
  (vld/rule (vld/has-value? date) [:edate "Event should have date"])
  (if-not 
    (vld/errors? :ename :eprice :edate) (do (db/add-event ename price date) (redirect "/user"))
    (index))
  )

(defn participate [req]
  ;(db/add-participate (sess/get :username) )
  (str req)
  )
