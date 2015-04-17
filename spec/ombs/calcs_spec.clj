(ns ombs.calcs-spec
  (:require [speclj.core :refer :all]
            [ombs.db :as db]))

(defn rand-rate []
  (if (<= (rand) 0.5)
    0.5
    1))


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

;(db/add-user "tester" "21.10.1999" 1.0)

(describe "Calculating:"
          (before
           (defn rand-rate []
             (if (<= rand 0.5)
               0.5
               1))
           (def event-price (rand-int 2000))
           (def users {:student1 { :rate rand-rate :name "Betty"}
                       :worker01 { :rate rand-rate :name "Mark"}
                       :student2 { :rate rand-rate :name "Joe"}
                       :worker02 { :rate rand-rate :name "Chendler"}}))

          (it "user debt, when many participants"
           (should= (let [ucount 5 ]
                      (/ event-price ucount))
                    0)))


(def users {
            :student1 { :rate (rand-rate) :name "Betty"}
            :worker01 { :rate (rand-rate) :name "Mark"}
            :student2 { :rate (rand-rate) :name "Joe" }
            :worker02 { :rate (rand-rate) :name "Chend ler"}})

(defn get-rate [user]
  (:rate
   (val user)))


(defn two-rate [u uu]

  (vector
   (get-rate u)
   (get-rate uu))
  )

 (val {:first {:rate 1}})


(two-rate {:first {:rate 1}} {:second {:rate 5}})

users

(defn plus-six [v]
  (+ 6 v))
users


(reduce #(+ (get-rate %1)) 0 users)

(run-specs)
