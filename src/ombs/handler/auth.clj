(ns ombs.handler.auth
  (:require  
    [ombs.view.common :as view]
    [ombs.db :as db]
    [ombs.handler.common :as handler]
    [ombs.core :as core]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [ombs.validate :as isvalid] )
  )

(defn reg-page [_] (view/register {}))

(defn log-user [uname]
  "Generate user page, with his name and events."
  (sess/put! :username uname)
  (redirect "/user"))

(defn register [{:keys [username password1 password2 birthdate student-flag] :as params} ]
  (println (str username " " password1" " password2" " birthdate" " student-flag " flog type" (type student-flag)))
  (if (isvalid/new-user? username password1 password2)
    (do
      (db/add-user username password1 birthdate (core/rate student-flag))
      (log-user username))
    (view/register params)))


(defn login [ { {uname :username pass :password :as params} :params} ]
  (if (isvalid/login? uname pass)
    (log-user uname)
    (handler/index))
  )

(defn logout [& _]
  (sess/clear!)
  (redirect "/")
  )