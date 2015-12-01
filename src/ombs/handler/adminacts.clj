(ns ombs.handler.adminacts
  (require
    [ombs.db.admin :as db]
    )
  )

(defn affirm [id] (db/affirm id))
