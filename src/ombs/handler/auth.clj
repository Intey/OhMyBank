(ns ombs.handler.auth
  (:require
    [ombs.db.user :as dbu]
    [ombs.core :as core]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [ombs.validate :as isvalid] )
  )

(defn log-user [uname]
  "Generate user page, with his name and events."
  (sess/put! :username uname))

(defn register [{:keys [username password1 password2 birthdate student-flag] :as params} ]
  (if (isvalid/new-user? username password1 password2)
    (do
      (dbu/add-user username password1 birthdate (core/rate student-flag))
      (log-user username))))

(defn login [ { {uname :username pass :password :as params} :params} ]
  (if (isvalid/login? uname pass) (log-user uname)))

(defn logout [& _]
  (sess/clear!))
