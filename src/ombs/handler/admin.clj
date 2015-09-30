(namespace ombs.handler.admin
           (:require 
             [ombs.db.admin :as db-a]  ))

(defn affirm [{fid :fid}] (println "affirming fee" (db-a/read-fee fid)))
