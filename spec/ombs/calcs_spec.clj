;(ns ombs.calcs-spec
;  (:require [clojure.math.numeric-tower :as math]
;            [speclj.core :refer :all]
;            [ombs.db :as db]))
;
;(defn rand-rate []
;  "return random rate: 1 or 0.5"
;  (if (<= (rand) 0.5)
;    0.5
;    1))
;
;(defn get-rate [user]
;  "Return rate for map like {:user {:rate 3}}"
;  (:rate
;   (val user)))
;
;(defn rates-sum [users]
;  "get map of users, where key - :user, and value - user data. User data should contains ':rate' pair"
;  (let [rates (map get-rate users)]
;    (reduce + rates)))
;
;(defn calc-user-debt [event-price users]
;  "calculate debt for rate=1 based on users rates and their count"
;  (math/round (math/ceil
;               (/ event-price (rates-sum users)) )))
;
;(describe "Database"
;          (before (db/add-user "Test User" "01.01.2000" 1.0 100)
;                  (defn keysort [map-] (into {} (sort-by key map-))))
;
;          (it "Get user - return user map with name, birthdate, rate and balance"
;              (should=
;               (keysort {:name "Test User" :bdate "01.01.2000" :rate 1.0 :balance 100})
;               (keysort (db/get-user "Test User"))))
;
;          (after (db/remove-user "Test User"))
;
;          (it "Username should be unique (db should throw error)"
;              (should-throw Exception (db/add-user "Test User" "01.01.2000" 1.0 100))))
;
;(describe "Calculating debt:"
;          (before-all
;           (db/add-event "Test Cookies" 254)
;           (db/add-event "Test Tea" 254)
;           (db/add-event "Test Cookies 2" 254)
;           (def event-price 800)
;           (def users {:student1 { :rate 0.5 :name "Betty"}
;                       :worker01 { :rate 0.5 :name "Mark"}
;                       :student2 { :rate 1 :name "Joe"}
;                       :worker02 { :rate 1 :name "Chendler"}})
;           (def username "Test User")
;           )
;          (it "Party pay for event price 800, participant: 4, with rates: [0.5, 0.5, 1, 1] should be 267"
;           (should= 267
;                    ;(calc-user-debt event-price users)
;                    (get-user-debt username eventname)
;                    ))
;
;          (it "User should have debt equal of sum of all debts of events, that he have participation"
;              (should= (get-user-debt username :full))
;
;              )
;          )
;
;(describe "Pay action:"
;          (before
;           (pay username event summ)
;          (it "when user pay some part, he should have remaining debt"
;              (should= 0 1)
;              )
;          (it "when user pay debt(part or full, event should change remainig"
;              (should= 0 1)
;              )
;
;(run-specs)
