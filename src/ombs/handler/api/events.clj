(ns ombs.handler.api.events
  (:require
    [clojure.string :as s]
    [ombs.db.event :as dbe]
    [ombs.db.payment :as dbp]
    [cheshire.core :as json]
    [ombs.funcs :refer [with-log]]
    ))

(defn all [& [types]]
  (if-let [parsed (json/parse-string types true)]
    (dbe/get-events (mapv keyword parsed)))
  (dbe/get-events))
