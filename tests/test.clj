(ns tests.test
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
  (sh "bash" "-c" "./resetdb.sh test"))

(def uid 1)
(def eid-solid 2)
(def eid-partial 3)

(def content-json "application/json")

(defn apireq
  ([method path] (mock/content-type (mock/request method path ) content-json))
  ([method path params] (mock/content-type (mock/request method path params) content-json)))

(defn mock-resp [body]
  {:status 200
   :headers {"Content-Type" content-json "Accept" content-json}
   :body (json/generate-string body)
   }
  )

(defmacro check-get== [query expected]
  `(t/should==
    (json/parse-string (:body (mock-resp ~expected)))
    (json/parse-string (:body (engine (apireq :get ~query))))) )
