(ns ombs.auth
  (:require  
    [ombs.view :as view]
    [ombs.db :as db]
    [ombs.handler :as handler]
    [ombs.ddm :as ddm]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [noir.validation :as vld] )
  )

(defn regpage [_] (view/register {}))

(defn log-user [uname]
  "Generate user page, with his name and events."
  (sess/put! :username uname)
  (redirect "/user")
  )

(defn register [params]
  (if (ddm/reg-ok? (:username params) (:password1 params) (:password2 params))
    ;true
    (do
      (db/add-user (:username params) (:password1 params) (:birthdate params) (ddm/rate (:student-flag params)))
      (log-user (:username params)) )
    ;fasle
    (view/register params)
    ))


(defn login [ { {uname :username pass :password :as params} :params} ]
  (vld/rule (vld/has-value? uname) [:uname "Username can't be empty"])
  (vld/rule (vld/has-value? (:name (db/get-user uname))) [:uname "User not found"])
  (vld/rule (= pass (:password (db/get-user uname))) [:upassword "Incorrect Login or password"])
  (if-not (vld/errors? :uname :upassword)
    (log-user uname)
    (handler/index))
  )

(defn logout [& _]
  (sess/clear!)
  (redirect "/")
  )
