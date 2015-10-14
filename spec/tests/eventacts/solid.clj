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

(spec/describe "======= Pay button on"

               (spec/before-all
                 (println "############################## RESET DB #######################################")
                 (sh "bash" "-c" "./resetdb.sh test"))

               (spec/context "=======solid event(without parts)"
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
                                      ))

               (spec/context "=======partial event(without parts)"
                             (spec/it "is HIDDEN, when user NOT participate event"
                                      (spec/should-not (dbp/can-pay? uid eid-partial))
                                      (spec/should (= 8 (db/get-rest-parts eid-partial))) 
                                      )

                             (spec/it "is VISIBLE, when user participated and NOT create fee (not press pay)"
                                      (nil? (println "Participate and start event " (db/get-event eid-partial)))
                                      (dbp/add-participant uid eid-partial)
                                      (core/start-event eid-partial)
                                      (spec/should (dbp/can-pay? uid eid-partial)) )

                             (spec/it "is VISIBLE, when user create fee for 4/8 parts (had press pay)"
                                      (dbp/create-fee uid eid-partial 4)
                                      (spec/should (dbp/can-pay? uid eid-partial)))

                             (spec/it "is VISIBLE, after admin confirm user fee (4/8 parts)"
                                      (affirmation (dba/get-fid uid eid-partial))
                                      (spec/should (dbp/can-pay? uid eid-partial))
                                      )

                             (spec/it "is HIDDEN, when all parts is in fees & pays"
                                      (dbp/create-fee uid eid-partial 4)
                                      (spec/should-not (dbp/can-pay? uid eid-partial)) 
                                      )

                             (spec/it "is HIDDEN, after admin confirm user fee (8/8 parts)"
                                      (affirmation (dba/get-fid uid eid-partial))
                                      (spec/should-not (dbp/can-pay? uid eid-partial))
                                      )
                             ))

(spec/run-specs)
