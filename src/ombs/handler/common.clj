(ns ombs.handler.common
  (:require
    [ombs.view.common :as commonview]
    [ombs.db :as db]
    [ombs.core :as core]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [noir.validation :as vld]
    ))

(defn print-it
  "just print request. Debug method."
  [request]
  (str request))

(defn index [& [params]]
  "Handler. show index page with events."
  (commonview/index (assoc params :events (db/get-events-list)))
  )

( defn user [& _]
  (if-let [username (sess/get :username)] ; if any user logged
    (commonview/user (core/event-users))
    (redirect "/"))
  )
