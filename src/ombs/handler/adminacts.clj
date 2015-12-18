(ns ombs.handler.adminacts
  (require
    [ombs.db.admin :as db]
    [ombs.validate :as isvalid]
    [cheshire.core :as ch]
    ))

(defn affirm [id]
  (println "affirm " id)
  (if (isvalid/fee? id)
    (db/affirm id)
    (ch/generate-string {:error (isvalid/errors-string)})))

(defn refute [id]
  (println "refute " id)
  (if (isvalid/fee? id)
    (db/refute id)
    (ch/generate-string {:error (isvalid/errors-string)})))
