(ns tests.eventacts.solid
  (:require [tests.test :refer :all]
            [clojure.test :as t]
            [clojure.java.shell :refer [sh]]
            [speclj.core :as spec]
            [ombs.dbold :as db]
            [ombs.db.admin :as dba]
            [ombs.db.payment :as dbp]
            [ombs.core :as core]
            ))

(spec/describe "Pay button on solid event(without parts)"
               (spec/before-all
                 (println "############################## RESET DB #######################################")
                 (sh "bash" "-c" "./resetdb.sh test")
                 )
               (spec/it "is hidden, when user NOT participate event"
                        (spec/should-not (dbp/can-pay? uid eid-solid)))

               (dbp/add-participant uid eid-solid)
               (core/start-event eid-solid)

               (spec/it "is visible, when user participated and fees sum not cover party-pay"
                        (spec/should (dbp/can-pay? uid eid-solid)) )
               (dbp/create-fee uid eid-solid))

(spec/run-specs)
; (t/use-fixtures :each cleandb-fixture)
; 
; (t/deftest can-pay
;   (t/is (= false (dbp/can-pay? uid eid-solid)))
;   (dbp/add-participant uid eid-solid)
;   (t/is (= true (dbp/can-pay? uid eid-solid)))
;   (dbp/create-fee uid eid-solid)
; 
;   )

