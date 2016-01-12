(ns tests.api.GET.events
  (:require [tests.test :refer :all]
            [clojure.java.shell :refer [sh]]
            [speclj.core :as t]
            ;[ring.mock.request :as mock]
            [ombs.route :refer [engine] ]
            [ombs.funcs :refer [date]]
            [cheshire.core :as json]
            ))

(t/describe "When GET"
            (t/before-all
              (println "############################## RESET DB #######################################")
              (sh "bash" "-c" "./scripts/resetdb.sh test")
              )
            (t/it "ALL events, SHOULD return vector"
                  (t/should==
                    (json/parse-string (:body (mock-resp
                      [{:id 1 :name "Cookies" :date (date) :price 124.0
                        :author "Intey" :status "in-progress" :parts 0 :rest 0}
                       {:id 2 :name "Tea" :date (date) :price 50.0
                        :author "andreyk" :status "in-progress" :parts 0 :rest 0}
                       {:id 3 :name "Pizza" :date (date) :price 1300.0
                        :author "Intey" :status "initial" :parts 8 :rest 8 }
                       {:id 4 :name "Waffles" :date (date) :price 3000.0
                        :author "Intey" :status "finished" :parts 8 :rest 0 }
                       ])))

                    (json/parse-string (:body (engine (apireq :get "/api/events"))))
                    )
                  )
            (t/it "ACTIVE events, SHOULD return events with status 'active'"
                  (t/should==
                    (json/parse-string (:body (mock-resp
                             [{:id 1 :name "Cookies" :date (date) :price  124.0
                               :author "Intey" :status "in-progress" :parts 0 :rest 0}
                              {:id 2 :name "Tea" :date (date) :price 50.0
                               :author "andreyk" :status "in-progress" :parts 0 :rest 0}])))
                    (json/parse-string (:body (engine (apireq :get "/api/events?type=active"))))
                    )
                    )
            (t/after-all
              (println "############################## RESET DB #######################################")
              (sh "bash" "-c" "./scripts/resetdb.sh test")))
