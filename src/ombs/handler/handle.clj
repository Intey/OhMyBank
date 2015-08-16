(ns ombs.handler.handle
  (:require
    [ombs.view.pages :as pages]
    [ombs.db :as db]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    ))

(defn index [] 
  (pages/index))

(defn user []
  (if-let [username (sess/get :username)]
    (pages/user username)
    (redirect "/")))

(defn addevent []
  (pages/addevent (db/get-usernames)))
