(ns ombs.handler.api.events
  (:require
    [clojure.string :as s]
    [cheshire.core :as json]
    [ombs.db.event :as dbe]
    [ombs.db.payment :as dbp]
    [ombs.funcs :refer [with-log]]
    ))

(defn get-events
  "Gets json array of events types(in-progress, finished, initial).  Return
  events with type in given types."
  ([] (dbe/get-events))
  ([types] (dbe/get-events (mapv keyword types))))

(defn new-event
  ([{:keys [name date price author &[parts]] :as params}]
   (dbe/add-event name date price author parts))
  (
   [{:keys [name date price author &[parts]] :as event} participants]
   (let [eid (dbe/add-event name date price author parts)]
     (dbp/add-participants event participants)
     ))
  )
