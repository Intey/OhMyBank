(ns tests.eventacts.solid
  (:require [tests.test :refer :all]
            [clojure.test :as t] 
            [ombs.dbold :as db]
            [ombs.db.admin :as dba]
            [ombs.db.payment :as dbp]
            ))

(t/use-fixtures :each cleandb-fixture)

(t/deftest can-pay
  (t/do-report "can-pay yep yep")
  (dbp/add-participant uid eid)
  (t/is (= true (dbp/can-pay? uid eid-solid)))
  (dbp/create-fee uid eid-solid)
  
  )
