(ns ombs.calcs-spec
  (:require [speclj.core :refer :all]
            [ombs.db :as db]))
(describe "Database getters should:"
          (before db/add-user "Test User" "01.01.2000" 1 100)
          (it "Get user - return user map with name, birthdate, rate and balance"
              (should true)
              ;(should= db/get-user "Test User" {:name "Test User" :birthdate "01.01.2000" :rate 1 :balance 100})
              )
          )
(describe "Calculating:"
          
          (it "user debt"
              (should true))
          )

(run-specs)
