(ns tests.api.GET.events
  (:require [tests.test :refer :all]
            [clojure.java.shell :refer [sh]]
            [speclj.core :as t]
            ;[ring.mock.request :as mock]
            [ombs.route :refer [engine] ]
            [ombs.funcs :refer [date]]
            [cheshire.core :as json]
            ))

(t/describe "When requested"

            (t/before-all
              ;(println "############################## RESET DB #######################################")
              ;(sh "bash" "-c" "./scripts/resetdb.sh test")
              )

            (t/it (str "not exists path, SHOULD: Content-Type == "
                       "'application/json', status == 404")
                  (t/should=
                     {:status 404
                      :headers {"Content-Type" content-json} }
                     (select-keys
                       (engine (apireq :get "/unexists"))
                       [:status :headers])))

            (t/it "All events, SHOULD return vector"
                  (t/should=

                    {:status 200
                     :headers {"Content-Type" content-json}
                     :body
                     (json/generate-string
                       [
                        {:id 1 :name "Cookies" :date (date) :price  124.0
                         :author   "Intey" :status "initial" :parts 0 :rest 0 }
                        {:id 2 :name     "Tea" :date (date) :price   50.0
                         :author "andreyk" :status "initial" :parts 0 :rest 0 }
                        {:id 3 :name   "Pizza" :date (date) :price 1300.0
                         :author   "Intey" :status "initial" :parts 8 :rest 8 }
                        ]) }

                    (engine (apireq :get "/api/events"))
                    )))
