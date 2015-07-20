(ns ombs.trash
   (:require
    [ombs.db :as db]
    [noir.session :as sess]
    [noir.response :refer [redirect]]
    [noir.validation :as vld]
     ))

; TODO: not fixed, after realize participation on addition
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
        (redirect "/user"))
      (str (vld/get-errors :user-exist :event-exist)) )))
