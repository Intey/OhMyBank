(ns tests.test
  (:require
    [clojure.java.shell :refer [sh]]
    [korma.core :as sql]
    [ombs.dbold :as db]
    ))

(defn cleandb-fixture [f]
  (sh "bash" "-c" "./resetdb.sh test")
  (f))

(def uid 1)
(def eid-solid 2)
(def eid-partial 3)
