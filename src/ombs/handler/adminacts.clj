(ns ombs.handler.adminacts
  (require 
    [ombs.db.admin :as db]
    )
  )

(defn affirm [id]
  (println "affirm fee-id:" id)
  (db/affirm id)
  "Affirmed!"
  )
