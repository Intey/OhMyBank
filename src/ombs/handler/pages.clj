(ns ombs.handler.pages
  (:require
    [ombs.view.pages :as pages]
    [ombs.view.admin :as admin]
    [ombs.dbold :as db]
    [ombs.core :as core]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    ))

(defn index [] (pages/index))

(defn user []
  (if-let [username (sess/get :username)]
    
    (if (= 1 (core/get-role username))
      (admin/page username) 
      (pages/user username))
    (redirect "/")))

(defn addevent []
  (if-let [username (sess/get :username)]
    (pages/addevent (db/get-usernames))
    (redirect "/")))
