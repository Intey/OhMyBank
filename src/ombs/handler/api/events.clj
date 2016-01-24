(ns ombs.handler.api.events
  (:require
    [cheshire.core :as json]
    [ombs.db.event :as dbe]
    [ombs.db.payment :as dbp]
    [ombs.funcs :refer [with-log parse-int]]
    [compojure.api.sweet :as swt]
    [schema.core :as s]
    [ring.util.http-response :as resp]
    ))

(s/defschema Event
  {:id s/Int
   :author s/Str
   :name s/Str
   :date s/Str
   :price s/Num
   :rest s/Num
   :parts s/Num
   :status s/Str })

(defn exists? [id] (dbe/exists? id))

(defn get-event [id]
  ;(if (exists? id)
    (dbe/get-event id)
    ;(resp/bad-request (errorId "Invalid id"))
    ;)

)

(defn get-events
  "Gets json array of events types(in-progress, finished, initial).  Return
  events with type in given types."
  ([] (dbe/get-events))
  ([types] (dbe/get-events (mapv keyword types))))

(defn new-event
  ([{:keys [name date price author &[parts]] :as params}]
   (dbe/add-event name date price author parts))
  ([{:keys [name date price author &[parts]] :as event} participants]
   (let [eid (dbe/add-event name date price author parts)]
     (dbp/add-participants event participants))))

(defn participants [id] (dbp/get-participants id))

(defn participate [eid username] (dbp/add-participants eid username))
