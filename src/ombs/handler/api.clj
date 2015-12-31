(ns ombs.handler.api
  (:require
    [clojure.string :as s]
    [ombs.db.old :as db]
    [ombs.db.barcheck :as dbb]
    [ombs.db.payment :as dbp]
    [cheshire.core :as json]
    [ombs.funcs :refer [with-log]]
    ))

(defn help []
  (json/generate-string
    {
     :help "Print this help"
     :get-events "Gets json array of events types(in-progress, finished,
                 initial).  Return events with type in given types."
     }))

(defn get-events
  "Gets json array of events types(in-progress, finished, initial).  Return
  events with type in given types."
  [types]
  (if-let [parsed (json/parse-string types true)]
    (db/get-events (mapv keyword parsed)))
  (db/get-events))

(defn get-barcheck [date] (dbb/get-barcheck date))
