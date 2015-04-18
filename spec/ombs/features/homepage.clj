(ns ombs.features.homepage
  (:require
    [speclj.core :as spec]
    [ring.adapter.jetty :as jetty]
    [clj-webdriver.taxi :as taxi]
    [ombs.features.config :refer :all]
    [ombs.routes :refer [main-routes]]))

(defonce server (jetty/run-jetty #'main-routes {:port 3000 :join? false}))
(defn start-browser []
  (try (taxi/set-driver! {:browser :chrome})
    (catch Exception e e)))

(defn stop-browser [] (taxi/quit))

(.start server)
(start-browser)

(spec/describe "Homepage"
               (spec/before
                 (taxi/to test-base-url))
               (spec/it "should have WELCOME text"
                        (spec/should=  "WELCOME" (taxi/text "#mainspan"))))

(spec/describe "logining"
               (spec/before
                 (def uname "intey")
                 (def password "password")
                 (def error-no-user (str "User" uname "not exists"))
                 (spec/before
                   (taxi/to test-base-url))
                 )
               (spec/it "should display move user to home, after sucess log in"
                        ;(taxi/input-text "#username" uname)
                        ;(taxi/input-text "#pass" password)
                        (spec/should= uname (taxi/text "#username"))

                        )
               (spec/it "should error when user not found"
                        (spec/-fail "but no error") )
               (spec/it "should error when password is wrong"
                        (spec/-fail "but no error") )
               (spec/it "should move user to home after registration. Should add user in db"
                        (spec/-fail "but no go to home")
                        (spec/-fail "but no add user in db") )

               )
(stop-browser)
(.stop server)

(spec/run-specs)
