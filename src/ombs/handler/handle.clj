(ns ombs.handler.handle
  (:require
    [ombs.view.pages :as pages]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    ))

(defn index [& [params]] 
  (pages/index))

(defn user [& _]
  (if-let [username (sess/get :username)]
    (pages/user username)
    (redirect "/")))
