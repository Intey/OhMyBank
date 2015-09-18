(ns ombs.handler.pages
  (:require
    [ombs.view.pages :as pages]
    [ombs.dbold :as db]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    ))

(defn index [] (pages/index))

(defn user []
  (if-let [username (sess/get :username)]
    (pages/user username)
    (redirect "/")))

(defn addevent []
  (if-let [username (sess/get :username)]
    (pages/addevent (db/get-usernames))
    (redirect "/")))

