(ns ombs.handler.api
  (:require
    [clojure.string :as s]
    [ombs.db.event :as dbe]
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
  ([] (dbe/get-events))
  ([types]
   (if-let [parsed (json/parse-string types true)]
     (dbe/get-events (mapv keyword parsed)))))


(def okRes (json/generate-string {:ok true}))

(def errorRes (json/generate-string {:error "Some error occur"}))
