(ns ombs.handler
  (:require
    [ombs.view.common :as vc]
    [ombs.view.event :as ve]
    [ombs.db :as db]
    [ombs.core :as core]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [noir.validation :as vld]
    ))

(defn print 
  "just print request. Debug method."
  [request] 
  (str request))

(defn index [& [params]]
  "Handler. show index page with events."
  (vc/index (assoc params :events (db/get-events-list)))
  )

( defn user [& _]
  (if-let [username (sess/get :username)] ; if any user logged
    (vc/user (core/event-users))
    (redirect "/"))
  )

(defn participate [{{ename :event-name} :params}]
  "Add participation of current user and selected event(given as param from post)"
  (let [uname (sess/get :username)
        uid (db/get-uid uname)
        eid (db/get-eid ename)]
    (vld/clear-errors!)
    (vld/rule (vld/has-value? uid) [:user-exist "User " uname " not found in database"])
    (vld/rule (vld/has-value? eid) [:event-exist (str "Event " ename " not found in database")])
    (if-not (vld/errors? :user-exist :event-exist)
      (do
        (if-not (nil? (db/add-participate uid eid))
          (str (assoc {} :ok (str "Now, user " uname " participate in event \"" ename "\""))))
          (redirect "/user")
        )
      (str (vld/get-errors :user-exist :event-exist)) )
    )
  )


(defn addevent-page [& [params]]
    (ve/addevent-page (db/get-usernames) )
  )
