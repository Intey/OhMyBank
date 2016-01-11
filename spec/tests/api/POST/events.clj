(ns tests.api.POST.events
  (:require [tests.test :refer :all]
            [clojure.java.shell :refer [sh]]
            [speclj.core :as t]
            ;[ring.mock.request :as mock]
            [ombs.route :refer [engine] ]
            [cheshire.core :as json]
            ))

(t/describe "When POST "

            (t/before-all
              (println "############################## RESET DB #######################################")
              (sh "bash" "-c" "./scripts/resetdb.sh test")
              )

            (t/it (str "maybe something real...")
                  (t/should=
                    false
                    true
                    ))

            (t/it "I don't know...something..."
                  (t/should=
                    true
                    false
                    ))

            (t/after-all
              (println "############################## RESET DB #######################################")
              (sh "bash" "-c" "./scripts/resetdb.sh test")
              ))
