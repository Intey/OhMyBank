(ns ombs.handler.auth
  (:require  
    [ombs.view.pages :as page]
    [ombs.db :as db]
    [ombs.handler.handle :as handler]
    [ombs.core :as core]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [ombs.validate :as isvalid] )
  )

(defn reg-page [] 
  "Hacky func, for open register-page. In routes i use only handler function, so here it is. In view.pages, 
  function register-page takes map with params - if you mistake input, it's redirect and already inserted 
  data was cached."
  (page/register {}))

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
    (page/register params)))


(defn login [ { {uname :username pass :password :as params} :params} ]
  (if (isvalid/login? uname pass)
    (log-user uname)
    (handler/index))
  )

(defn logout [& _]
  (sess/clear!)
  (redirect "/")
  )
