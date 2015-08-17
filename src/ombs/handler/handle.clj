(ns ombs.handler.handle
  ^{:deprecated 0.0 :doc "Deprecated after 59b6beb. Use in routers views."}
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
