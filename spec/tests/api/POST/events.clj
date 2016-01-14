(ns tests.api.POST.events
  (:require [tests.test :refer :all]
            [clojure.java.shell :refer [sh]]
            [speclj.core :as t]
            ;[ring.mock.request :as mock]
            [ombs.funcs :refer [date]]
            [ombs.route :refer [engine] ]
            [cheshire.core :as json]
            ))

(t/describe "When POST"
            (t/before-all
              (println "############################## RESET DB #######################################")
              (sh "bash" "-c" "./scripts/resetdb.sh test")
              )
            (t/with-all! event {:name "Hookers" :price 6000.0 :date (date)})

            (t/context "new event"
              (t/it "should return event row with id"
                  (let [response (select-keys
                                 (engine (apireq :post "/api/events" (json/generate-string @event)))
                                 [:id :name :price :date])]
                    (t/should== @event response)
                    (t/should-not-be-nil (:id response))))

              (t/it "with participants SHOULD return event id and notify users"
                  (let [participats ["Intey" "andreyk"]
                        response (engine (apireq :post "/api/events"
                            (json/generate-string
                              (assoc @event :participants participats))))]
                    (t/should== participats (get :participants response))
                    (t/should-not-be-nil (:id response))))
              )


            (t/context "array of events"
                       (let [response
                             (json/parse-string (:body (engine (apireq :post "/api/events" {}))))]
                         (t/it "it SHOULD return vector added events"
                               (t/should-be seq? response)
                               (t/should= 2 (count response))
                               (t/should (every? true?
                                 (map number? (map :id response)))))))

            (t/after-all
              (println "############################## RESET DB #######################################")
              (sh "bash" "-c" "./scripts/resetdb.sh test")))

