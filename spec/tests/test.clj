(ns tests.test
  (:require
    [clojure.java.shell :refer [sh]]
    [korma.db :as kdb]
    [korma.core :as sql]
    [ring.mock.request :as mock]
    ))

(kdb/defdb korma-db (kdb/sqlite3
                      { :db "test.db"
                       :user "user"
                       :password "placeholder"}))

(defn cleandb-fixture [f]
  (sh "bash" "-c" "./resetdb.sh test")
  (f))

(def uid 1)
(def eid-solid 2)
(def eid-partial 3)

(def content-json "application/json; charset=utf-8")

(defn apireq [method path]
  (mock/content-type (mock/request method path) content-json))
