(ns ombs.server.restapi.response
  (require
    [cheshire.core :as ch]
    [ombs.validate :as isvalid]
    [ombs.dbold :as db]
    [ombs.view.actions :as acts]
    ))

(defprotocol ISerializable
  (serialize [d] "Serialize d(ata) to JSON representation"))

(defprotocol IBelongsToEvent
  (get-event [id] "Generate correspond event map(record).")
  (get-availible-action [id] "Prepare availible actions for event, that
                             correspond to this fee")
  )

;defmulty?
(def serialize
  "Default serialization. Just cheshire.core.generate-string wrapper"
  (reify
    ISerializable
    (serialize [d] (ch/generate-string d))))

(defrecord ResponseError [error])

(defrecord ResponseData [data])

(defrecord ResponseAction [data &args])

(defrecord FeeResponse [id]
  IBelongsToEvent
  (get-event [this] (db/event-from-fee (:id this)))
  (get-availible-action [this] (acts/get-action (.get-event this))))
