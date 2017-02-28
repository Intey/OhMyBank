(ns tests.event
  (:require
    [speclj.core :as t]
    [tests.helpers :refer :all]
    [ombs.event.db :as db]
    ))

(t/describe "When call db-fn"
  (t/context "get-events"
    (t/it "without params should return all events with participants names"
      (t/should-not-be-nil (db/get-events)))
    (t/it "with types vector should return events with it's type"
      (t/should
        (every? #(not= (:status %) :initial)
                (db/get-events [:in-progress :finished]))))
    )

  (t/context "participate"
    (t/it "with single username should add participant"
          true
          )
    )

  )
