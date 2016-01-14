(ns ombs.handler.api.events
  (:require
    [clojure.string :as s]
    [ombs.db.event :as dbe]
    [ombs.db.payment :as dbp]
    [cheshire.core :as json]
    [ombs.funcs :refer [with-log]]
    ))

(defn get-events
  "Gets json array of events types(in-progress, finished, initial).  Return
  events with type in given types."
  ([] (dbe/get-events))
  ([types]
   (if-let [parsed (json/parse-string types true)]
     ((dbe/get-events (mapv keyword parsed))))))

(defn new-event [{:keys [name date price author participats parts] :as params}]
  (println (str "new event " params))
  (let [eid (dbe/add-event name date price author parts participats)]
    )

  )

