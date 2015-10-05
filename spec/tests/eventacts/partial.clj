(ns tests.eventacts.partial
  (:require [tests.test :refer :all]
            [clojure.test :as t]
            [ombs.dbold :as db]
            [ombs.db.admin :as dba]
            [ombs.db.payment :as dbp]
            ))

(t/use-fixtures :each cleandb-fixture)

(t/deftest rest-parts
  (t/is (= 8 (db/get-rest-parts eid-partial))))

(t/deftest rest-part-with-fees
  (dbp/create-fee uid eid-partial 4)
  (t/is (= 4 (db/get-rest-parts eid-partial)))
  )

(t/deftest can-pay
  (t/is (= false (dbp/can-pay? uid eid-partial)))
  (dbp/add-participant uid eid-partial)
  (t/is (= true  (dbp/can-pay? uid eid-partial)))
  (dbp/create-fee uid eid-solid)
  (t/is (= false (dbp/can-pay? uid eid-partial)))
  )
