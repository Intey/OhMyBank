(ns tests.eventacts.solid
  (:require [tests.test :refer :all]
            [clojure.test :as t]
            [clojure.java.shell :refer [sh]]
            [speclj.core :as spec]
            [ombs.dbold :as db]
            [ombs.db.admin :as dba]
            [ombs.db.payment :as dbp]
            [ombs.core :as core]
            [noir.validation :as vld]
            ))
(def affirmation (vld/wrap-noir-validation dba/affirm))

(spec/describe "Pay button on solid event(without parts)"
               (spec/before-all
                 (println "############################## RESET DB #######################################")
                 (sh "bash" "-c" "./resetdb.sh test"))

               (spec/it "is HIDDEN, when user NOT participate event"
                        (spec/should-not (dbp/can-pay? uid eid-solid)))

               (spec/it "is VISIBLE, when user participated and  NOT create fee (not press pay)"
                        (nil? (println "Participate and start event " (db/get-event eid-solid)))
                        (dbp/add-participant uid eid-solid)
                        (core/start-event eid-solid)
                        (spec/should (dbp/can-pay? uid eid-solid)) )

               (spec/it "is HIDDEN, when user create fee (had press pay)"
                        (dbp/create-fee uid eid-solid)
                        (spec/should-not (dbp/can-pay? uid eid-solid)))
               (spec/it "is HIDDEN, after admin confirm user fee"
                        (affirmation (dba/get-fid uid eid-solid))
                        (spec/should-not (dbp/can-pay? uid eid-solid))
                        )
               )

(spec/run-specs)
