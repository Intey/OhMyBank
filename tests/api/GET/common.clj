(ns tests.api.GET.common
  (:require [tests.test :refer :all]
            [clojure.java.shell :refer [sh]]
            [speclj.core :as t]
            ;[ring.mock.request :as mock]
            [ombs.route :refer [engine] ]
            [ombs.funcs :refer [date]]
            [cheshire.core :as json]
            ))

(t/describe "When POST"
            (t/it (str "not exists path, SHOULD: Content-Type == "
                       "'application/json', status == 404")
                  (t/should==
                    {:status 404
                     :headers {"Content-Type" content-json} }
                    (select-keys
                      (engine (apireq :get "/unexists"))
                      [:status :headers]))))
