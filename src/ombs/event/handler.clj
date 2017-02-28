(ns ombs.event.handler
  (:require
    [cheshire.core :as json]
    [schema.core :as s]
    [ring.util.http-response :as resp]

    [ombs.event.db :as dbe]
    [ombs.event.validator :refer [errorId]]
    [ombs.db.payment :as dbp]
    [ombs.validate :as vld]
    [ombs.funcs :refer [with-log parse-int]]
    ))


(defn get-event [id]
  (if (dbe/exists? id)
    (resp/ok (dbe/get-event id))
    (resp/bad-request (errorId "Invalid id"))))

(defn get-events
  "Gets json array of events types(in-progress, finished, initial).  Return
  events with type in given types."
  ([types] (resp/ok (if (nil? types)
                      (dbe/get-events)
                      (dbe/get-events (mapv keyword types))))))

(defn new-event
  ([{:keys [name date price author &[parts]] :as params}]
   (dbe/add-event name date price author parts))
  ([{:keys [name date price author &[parts]] :as event} participants]
   (let [eid (dbe/add-event name date price author parts)]
     (dbp/add-participants event participants))))

(defn participants [id] (dbp/get-participants id))

(defn participate [eid username]
  (if-let [errs (vld/participation? eid username)]
    (resp/bad-request errs)
    (resp/ok (dbp/add-participant eid username))))
