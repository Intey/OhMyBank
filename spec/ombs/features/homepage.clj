(ns ombs.features.homepage
  (:require 
    [speclj.core :refer :all]
    [ring.adapter.jetty :refer [run-jetty]]
    [clj-webdriver.taxi :refer :all]
    [ombs.features.config :refer :all]
    [ombs.routes :refer [main-routes]]))

(defonce server (run-jetty #'main-routes {:port 3000 :join? false}))

(defn start-browser [] (set-driver! {:browser :chrome}))
(defn stop-browser [] (quit))

(describe "homepage"
          (before 
            (.start server)
            (start-browser)
            (to test-base-url))

          (it "should have WELCOME text"
              (should= (text "#mainspan") "WELCOME"))

          (after (stop-browser)
                 (.stop server)))

(run-specs)
