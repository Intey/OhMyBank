(ns tests.api.helpers
  (:require [tests.test :refer :all]
            [clojure.java.shell :refer [sh]]
            [speclj.core :as t]
            [ring.mock.request :as mock]
            [ombs.route :as api]
            [cheshire.core :as json]
            ))


(t/describe "Helper"
  (t/it "wrap-content-json should generate response with Content-Type 'application/json'"
    (t/should=
      content-json
      (get-in (api/engine (mock/content-type (mock/request :get "/api/test") "text/html" )) [:headers "Content-Type"])))
)

;((api/wrap-content-json (fn [req] req)) (mock/content-type (mock/request :get "api/test") "text/html"))
