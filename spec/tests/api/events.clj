(ns tests.eventacts.solid
  (:require [tests.test :refer :all]
            [clojure.java.shell :refer [sh]]
            [speclj.core :as t]
            [ring-mock :as mock]
            [ombs.route :refer [api]]
            [cheshire.core :as json]
            ))

(t/describe "When requested"

            (spec/before-all
              (println "############################## RESET DB #######################################")
              (println (sh "bash" "-c" "./scripts/resetdb.sh test")))

            (t/it "All events, SHOULD return vector"
                  (= (api (mock/request :get "/events"))
                     {:status 200
                      :headers {"content-type" "application/json"}
                      :body
                      (json/generate-string [
                                             :id 1 :name "Cookies" :price  124 :author   "Intey" :state "initial" :parts 0
                                             :id 2 :name     "Tea" :price   50 :author "andreyk" :state "initial" :parts 0
                                             :id 3 :name   "Pizza" :price 1300 :author   "Intey" :state "initial" :parts 8
                                             ])
}))
