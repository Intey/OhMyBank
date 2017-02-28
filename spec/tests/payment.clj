(ns tests.payment
  (:require
    [speclj.core :as t]
    [tests.helpers :refer :all]
    [ombs.db.payment :as dbp]
    [ombs.event.db :as dbe]
    [ombs.user.db :as dbu]
    )
  )

(t/describe "When call db-fn"
  (t/before-all
    (println "############################## RESET DB #######################################")
    (println (cleandb))
    (dbe/add-event {:name "Cookies" :price 1200 :date "2015-10-12" :author "Intey"})
    (dbu/get-users)
    )

  (t/context "add-participants in empty event"
    (t/it "should move money from participant to author"
      (dbp/add-participants (dbe/get-event 1) [{:name "Intey" :rate 1}])
      (t/should-not
        (= (dbp/get-debt "Intey") 0)
        )
      )
    ))
