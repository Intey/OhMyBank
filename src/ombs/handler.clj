(ns ombs.handler
  (:require
    [ombs.view :as view]
    [ombs.db :as db]
    [ombs.ddm :as ddm]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [noir.validation :as vld]
    [org.httpkit.server :refer :all]
    ))

(defn index [& [params]]
  "Handler. show index page with events."
  (view/index (assoc params :events (db/get-events-list)))
  )

( defn user [& _]
  (if-let [username (sess/get :username)] ; if any user logged
    (view/user (ddm/event-users))
    (redirect "/"))
  )

(defn add-event [{{ename :name price :price date :date :as params} :params}]
  (str params)
  (vld/clear-errors!)
  (vld/rule (vld/has-value? ename) [:ename "Event name should not be empty"])
  (vld/rule (vld/greater-than? price 0) [:eprice "Event price should be greater than 0"])
  (vld/rule (vld/has-value? date) [:edate "Event should have date"])
  (if-not
    (vld/errors? :ename :eprice :edate) (do (db/add-event ename price date) (redirect "/user"))
    (index))
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
        (println (str "uid:" uid " | eid:" eid))
        (db/add-participate uid eid)
        (str (assoc {} :ok (str "Now, user " uname " participate in event \"" ename "\"")))
        )
      (str (vld/get-errors :user-exist :event-exist)) )
    )
  )
(defn ws-h [req]
  (with-channel req channel ; get the channel
    ;; communicate with client using method defined above
    (on-close channel (fn [status]
                        (println "channel closed")))
    (if (websocket? channel)
      "websocket"
      "HTTP channel")
    (on-receive channel (fn [data]       ; data received from client
                          ;; An optional param can pass to send!: close-after-send?
                          ;; When unspecified, `close-after-send?` defaults to true for HTTP channels
                          ;; and false for WebSocket.  (send! channel data close-after-send?)
                          (println (str "recieve: " data))
                          (send! channel data))))) ; data is sent directly to the client
