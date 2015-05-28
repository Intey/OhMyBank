(ns ombs.handler
  (:require
    [ombs.view :as view]
    [ombs.db :as db]
    [ombs.core :as core]
    [noir.session :as sess]
    [noir.response :refer [redirect] ]
    [noir.validation :as vld]
    ))

(defn index [& [params]]
  "Handler. show index page with events."
  (view/index (assoc params :events (db/get-events-list)))
  )

(defn regpage [_] (view/register {}))

(defn log-user [uname]
  "Generate user page, with his name and events."
  (sess/put! :username uname)
  (redirect "/user")
  )

(defn register [params]
  (if (core/reg-ok? (:username params) (:password1 params) (:password2 params))
    ;true
    (do 
      (db/add-user (:username params) (:password1 params) (:birthdate params) (core/rate (:student-flag params)))
      (log-user (:username params)) )
    ;fasle 
    (view/register params) 
    ))


(defn login [ { {uname :username pass :password :as params} :params} ]
  (vld/rule (vld/has-value? uname) [:uname "Username can't be empty"])
  (vld/rule (vld/has-value? (:name (db/get-user uname))) [:uname "User not found"])
  (vld/rule (= pass (:password (db/get-user uname))) [:upassword "Incorrect Login or password"])
  (if-not (vld/errors? :uname :upassword)  
    (log-user uname)
    (index))
  )

(defn logout [& _] 
  (sess/clear!)
  (redirect "/")
  )

( defn user [& _]
  (if-let [username (sess/get :username)]
    (view/user (db/get-user-events username))
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
