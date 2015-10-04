;(ns tests.features.homepage
;  (:require
;    [speclj.core :as spec]
;    [ring.adapter.jetty :as jetty]
;    [clj-webdriver.taxi :as taxi]
;    [ombs.features.config :refer :all]
;    [ombs.route :refer [main-routes]]))
;
;(defonce server (jetty/run-jetty #'main-routes {:port 3000 :join? false}))
;(defn start-browser []
;  (try (taxi/set-driver! {:browser :chrome})
;    (catch Exception e e)))
;
;(defn stop-browser [] (taxi/quit))
;
;
;(spec/describe "Homepage"
;               (spec/before-all
;                 (stop-browser)               
;                 (.stop server)   
;                 (.start server)
;                 (start-browser)
;
;                 (def uname "intey")
;                 (def password "password")
;                 (def error-no-user (str "User" uname "not exists"))
;                 )
;               (spec/before
;                   (taxi/to test-base-url))
;
;               (spec/it "should have Welcome text"
;                        (spec/should=  "Welcome" (taxi/text "#mainspan")))
;
;               (spec/it "should move user to home after registration. Should add user in db"
;                        (spec/-fail "but no go to home")
;                        (spec/-fail "but no add user in db") )
;               (spec/it "should error when user not found"
;                        (spec/-fail "but no error") )
;               (spec/it "should error when password is wrong"
;                        (spec/-fail "but no error") )
;
;               (spec/it "should move user to his page(with his name), after sucess log in"
;                        
;                          (taxi/input-text "#username" uname)
;                          (taxi/input-text "#pass" password)
;                          (taxi/click "#ok")
;                        (spec/should= (str test-base-url "user/intey") (taxi/current-url))
;                        (spec/should= uname (taxi/text "#user"))
;                        (spec/should= uname (taxi/title (str "User" uname)))
;
;                        )
;               (spec/after-all
;                 
;                )
;               )
;
;(spec/run-specs)
