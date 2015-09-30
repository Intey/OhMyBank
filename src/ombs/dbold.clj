(ns ombs.dbold
  (:require [korma.db :as kdb]
            [korma.core :as sql]
            [ombs.funcs :as f]
            ))

(kdb/defdb korma-db (kdb/sqlite3
                      { :db "database.db"
                       :user "user"
                       :password "placeholder"}))
; about many-to-many https://groups.google.com/d/msg/sqlkorma/r3kR6DyQZHo/RrQS_J8kkQ8J

(declare events)
(declare goods)
(declare fees)

(sql/defentity users
  (sql/many-to-many events :pays {:lfk :users_id :rfk :events_id})
  (sql/has-many fees)
  )

(sql/defentity events
  (sql/many-to-many users :pays {:lfk :events_id :rfk :users_id})
  (sql/has-one goods)
  )

(sql/defentity pays
  (sql/belongs-to events {:fk :events_id})
  (sql/belongs-to users {:fk :users_id}))

(sql/defentity fees
  (sql/belongs-to events)
  (sql/belongs-to users))


(sql/defentity goods
  (sql/has-many events {:fk :events_id}))

(sql/defentity summary)
(sql/defentity debts)

(sql/defentity participation)
(sql/defentity participants)

; statuses - describe status of event.
;   Initial - created, but not started. Collecting participants.
;   In-progress - Collecting money! Not full sum payed.
;   Finished - closed.
(def statuses {:initial "initial" :finished "finished" :in-progress "in-progress"})

; ===========================================================================================================
; ===========================================================================================================

;============================================== USER =================================================

(defn add-user [uname password birthdate rate]
  (sql/insert users (sql/values {:name uname :password password :bdate birthdate :rate rate } )))

(defn get-user [uname]
  "Return map of user info"
  (first (sql/select users (sql/fields :name :bdate :balance :rate :password)
           (sql/where (= :name uname))
           (sql/limit 1))))

(defn get-uid [uname]
  (:id (first (sql/select users (sql/fields :id)
                          (sql/where (= :name uname))))))

(defn get-rate [uname]
  (:rate (first (sql/select users (sql/fields :rate)
                            (sql/where (= :name uname))))))

(defn get-usernames [] (sql/select users (sql/fields :name)))

(defn get-rates [usernames] (map #(get-rate %) usernames))


;============================================== EVENT =================================================
(defn add-event
  ([ename price author date parts]
   (sql/insert events (sql/values {:name ename :price price :author author :date date :status (statuses :initial) :parts parts})))
  ([ename price author date]
   (sql/insert events (sql/values {:name ename :price price :author author :date date :status (statuses :initial) :parts 0}))))

(defn set-status 
  ([ename date s]
  (sql/update events (sql/set-fields {:status (statuses s)})
              (sql/where {:name ename :date date})))
  ([eid s]
  (sql/update events (sql/set-fields {:status (statuses s)})
              (sql/where {:id eid}))))

(declare subtract-feesed-parts)
(defn get-events []
  "Return list of events, and it actual count of event"
  ;First of all, we select events from it table and join for each rest(actual)
  ;parts Then we update each event elent: substract from each count of parts,
  ;that hang in fees some events haven't parts, and after join it's have nil
  ;parts. So we need fix before substract
  (map
    #(update % :parts 
             (comp (partial subtract-feesed-parts (:id %)) f/nil-fix))
    (sql/select events
                (sql/fields :id :name :date :price :author :status)
                (sql/with goods (sql/fields [:rest :parts])))))

(defn subtract-feesed-parts [eid parts] 
  "Substract from given parts, founded parts in active(all) fees."
  (assert (not= nil parts) "Can't subtract-feesed-parts from nil")
  (- parts 
     (-> (sql/select fees (sql/fields :sum) 
                     (sql/where {:events_id eid}) 
                     (sql/aggregate (sum :parts) :sum)) 
         (first) 
         (:sum) 
         (f/nil-fix))))

(defn get-active-events []
  (sql/select events
              (sql/where (not= :status (statuses :finished)))))

(defn get-event [ename date]
  (first (sql/select events
                     (sql/where (and (= :date date) (= :name ename))))))

(defn get-eid [ename date]
  (:id (first (sql/select events (sql/fields :id)
                          (sql/where (and (= :name ename) (= :date date)))))))

(defn get-price 
  ([ename date]
   (:price (first (sql/select events (sql/fields :price)
                              (sql/where (and (= :date date) (= :name ename)))))))
  ([eid]
   (:price (first (sql/select events (sql/fields :price) (sql/where {:id eid}))))))

(defn get-parts 
  ([ename date]
   (:rest (first (sql/select goods (sql/fields :rest)
                             (sql/where {:events_id (get-eid ename date)})))))
  ([eid]
   (:rest (first (sql/select goods (sql/fields :rest)
                             (sql/where {:events_id eid}))))))

 


(defn get-status [ename date]
  (:status (first (sql/select events (sql/fields :status)
                              (sql/where {:name ename :date date} )))))

(defn is-initial? [ename date]
   (= (statuses :initial)
      (:status (first (sql/select events (sql/fields :status)
                                  (sql/where {:name ename :date date} ) )))))
(declare get-rest-parts)
(declare price-diff)
(defn can-finish? 
  ([ename date]
   (zero? 
     (if (zero? (get-parts ename date))
       (get-rest-parts ename date) 
       (price-diff ename date))))
  ([eid]
   (zero? 
     (if (zero? (get-parts eid))
       (get-rest-parts eid) 
       (price-diff eid)))) 
  )

(defn- price-diff 
  ([ename date] 
  (reduce - 
          (replace 
            (first (sql/select summary (sql/where {:event ename :date date}))) 
            [:debits :credits])))
  ([eid]
  (reduce - 
          (replace 
            (first (sql/select summary (sql/with events (sql/fields :id)) (sql/where {:id eid}) )) 
            [:debits :credits]))) 

  )  

(defn finish 
  ([ename date] (set-status ename date :finished))
  ([eid] (set-status eid :finished))
  )
;============================================== GOODS  =================================================

;2 transacts
(defn add-goods [ename date parts]
  (sql/insert goods (sql/values {:events_id (get-eid ename date) :rest parts})))

;2 transacts
(defn get-rest-parts 
  ([ename date]
   (:rest (first (sql/select goods (sql/fields :rest)
                             (sql/where {:events_id (get-eid ename date)})))))

  ([eid]
   (:rest (first (sql/select goods (sql/fields :rest)
                             (sql/where {:events_id eid}))))))

;3 transacts
(defn shrink-goods 
  ([ename date parts]
   "Sub count parst from database"
   (println (str "shrink goods on" parts))
   (sql/update goods (sql/set-fields {:rest (- (get-parts ename date) parts)})
               (sql/where {:events_id (get-eid ename date)})))
  ([eid parts]
   (println (str "shrink goods on" parts))
   (sql/update goods (sql/set-fields {:rest (- (get-parts eid) parts)})
               (sql/where {:events_id eid})))

  )

(defn parts-price [eid parts]
  (* parts (f/part-price (get-price eid) (get-parts eid))))
