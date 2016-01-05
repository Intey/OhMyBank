(ns ombs.handler.adminacts
  (require
    [ombs.db.old :as db]
    [ombs.db.admin :as dba]
    [ombs.db.payment :as dbp]
    [ombs.validate :as isvalid]
    [cheshire.core :as ch]
    ))

(defn affirm [id]
  (println "affirm " id)
  (if (isvalid/fee? id)
    (dba/affirm id)
    (ch/generate-string {:error (isvalid/errors-string)})))

(defn refute [id]
  (println "refute " id)
  (dba/refute id)
  (ch/generate-string {:ok (str "removed fee " id)}))

(defn moneyout [username money]
  (println "moneyout " username " " money)
  (let [uid (db/get-uid username)]
    (if (isvalid/moneyout? uid money)
      (dbp/credit-payment uid db/moeid money)
      (ch/generate-string {:error (isvalid/errors-string)}))))
