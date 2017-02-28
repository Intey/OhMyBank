(ns tests.helpers
  (:require
    [clojure.java.shell :refer [sh]]
    [korma.db :as kdb]
    [korma.core :as sql]
    [ring.mock.request :as mock]
    [cheshire.core :as json]
    [ombs.route :refer [engine] ]
    ))

(kdb/defdb korma-db (kdb/sqlite3
                      { :db "test.db"
                       :user "user"
                       :password "placeholder"}))

(defn cleandb []
  (dorun (sh "bash" "-c" "scripts/resetdb.sh test")))
