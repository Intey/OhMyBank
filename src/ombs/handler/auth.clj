(ns ombs.handler.auth
  (:require
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [ombs.db.user :as dbu]
    [ombs.validate :as isvalid]
    [ombs.funcs :as f]
    )
  )

(defn log-user [uname]
  "Generate user page, with his name and events."
  (sess/put! :username uname))

(defn register [{:keys [username password1 password2 birthdate student-flag] :as params} ]
  (if (isvalid/new-user? username password1 password2)
    (do
      (dbu/add-user username password1 birthdate (f/rate student-flag))
      (log-user username))))

(defn login [ { {uname :username pass :password :as params} :params} ]
  (if (isvalid/login? uname pass) (log-user uname)))

(defn logout [& _]
  (sess/clear!))
