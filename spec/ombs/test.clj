(ns ombs.test
  (:require 
    [clojure.test :as t] 
    [ombs.dbold :as db]
            ))

(t/deftest rest-parts
  (t/is (db/get-rest-parts 3) ))
