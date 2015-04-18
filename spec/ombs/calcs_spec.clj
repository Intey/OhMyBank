(ns ombs.calcs-spec
  (:require [speclj.core :refer :all]
            [ombs.db :as db]))

(defn rand-rate []
  (if (<= (rand) 0.5)
    0.5
    1))

(defn get-rate [user]
  (:rate
   (val user)))

(defn rates-sum [users]
  "get map of users, where key - :user, and value - user data.
  User data should contains ':rate' pair"
  (let [rates (map get-rate users)]
  (reduce + rates)))

; (reduce #(+ (get-rate %1)) 0 users) ; gets summ of rate from map of map

(describe "Database"
          (before (db/add-user "Test User" "01.01.2000" 1.0 100)
                  (defn keysort [map-] (into {} (sort-by key map-))))

          (it "Get user - return user map with name, birthdate, rate and balance"
              (should=
               (keysort {:name "Test User" :bdate "01.01.2000" :rate 1.0 :balance 100})
               (keysort (db/get-user "Test User"))))

          (after (db/remove-user "Test User"))

          (it "Username should be unique (db should throw error)"
              (should-throw Exception (db/add-user "Test User" "01.01.2000" 1.0 100))))

(describe "Calculating:"
          (before
           (def event-price (rand-int 2000))

           (def users {:student1 { :rate (rand-rate) :name "Betty"}
                       :worker01 { :rate (rand-rate) :name "Mark"}
                       :student2 { :rate (rand-rate) :name "Joe"}
                       :worker02 { :rate (rand-rate) :name "Chendler"}})

           (println (str "\tusers: " users))
           (println (str "\tevent price: " event-price))
           (println (str "\trates sum: " (rates-sum users)))

           (println)
           )

          (it "For first user debt when many participants:"
           (should (/ event-price (rates-sum users))
            )))
(run-specs)
