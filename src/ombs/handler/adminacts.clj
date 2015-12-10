(ns ombs.handler.adminacts
  (require
    [cheshire.core :as ch]
    [ombs.db.admin :as dba]
    [ombs.dbold :as db]
    [ombs.validate :as isvalid]
    [ombs.view.actions :as acts]
    [ombs.server.restapi.response :refer :all]
    ))

(defn with-log [data] (println "affirmed. Response: " data) data)

(defn affirm [id]
  "Affirm fee with id: create payment row"
  (println "affirm fid: " id)
  (if (isvalid/fee? id)
    (when-let [event (db/event-from-fee id)]
      (dba/affirm id)
      (with-log (->ResponseData (acts/actions-html event)))
      )
    (->ResponseError (isvalid/errors-string))))

(defn decline [id]
  (println "decline fid: " id)
  (->ResponseError "Not implemented")
  )
