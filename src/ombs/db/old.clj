(ns ombs.db.old
  (:require [korma.db :as kdb]
            [korma.core :as sql]
            [ombs.funcs :as f]
            ))

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
  (sql/has-one fees)
  )

(sql/defentity pays
  (sql/belongs-to events {:fk :events_id})
  (sql/belongs-to users {:fk :users_id}))

(sql/defentity fees
  (sql/belongs-to events)
  (sql/belongs-to users))


(sql/defentity goods
  (sql/has-many events {:fk :events_id}))

; DATA VIEWS
(sql/defentity summary)
(sql/defentity debts)
(sql/defentity balances)
;FIXME: delete one
(sql/defentity participation)
(sql/defentity participants)

; statuses - describe status of event.
;   Initial - created, but not started. Collecting participants.
;   In-progress - Collecting money! Not full sum payed.
;   Finished - closed.
(def statuses {:initial "initial" :finished "finished" :in-progress "in-progress"})
(def admin-role-value 0)
(defn status-vector [ st ]
  (if (vector? st)
    (mapv #(% statuses) st)
    (st statuses)))
; ===========================================================================================================
; ===========================================================================================================

;============================================== USER =================================================

(defn add-user [uname password birthdate rate]
  (sql/insert users (sql/values {:name uname :password password :bdate birthdate :rate rate } )))

(defn get-user [uname]
  "Return map of user info"
  (first (sql/select balances (sql/where (= :name uname))
                     (sql/limit 1))))

(defn get-users []
  "Get usernames and their balances"
  (sql/select balances (sql/where (< 0 :balance))))

(defn get-uid [uname]
  (:id (first (sql/select users (sql/fields :id)
                          (sql/where (= :name uname))))))

(defn get-rate [uname]
  (:rate (first (sql/select users (sql/fields :rate)
                            (sql/where (= :name uname))))))

(defn get-usernames [] (sql/select users (sql/fields :name)))

(defn get-rates [usernames] (map #(get-rate %) usernames))

(defn admin? [username] (->
                          (sql/select users (sql/where {:name username :role admin-role-value}))
                          first nil?  not))

;============================================== EVENT =================================================
; (defrecord Event [name date price author status] )
; (defrecord PartialEvent [parts-count actual-parts])

(defn get-eid [ename date]
  (:id (first (sql/select events (sql/fields :id)
                          (sql/where (and (= :name ename) (= :date date)))))))

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
(defn get-events [status]
  "Return list of events, and it actual count of event"
  ; Then we update each event elent: substract from each event count of parts,
  ; that hang in fees.
  ; Some events haven't parts, and after join it's have nil parts. So we need
  ; fix before substract.
  (map
    #(update % :rest
             (comp (partial subtract-feesed-parts (:id %)) f/nil-fix))
    ; First of all, we select events from it table and join for each
    ; rest(actual) parts.
    (sql/select events
                (sql/fields :id :name :date :price :author :status :parts)
                (sql/where {:status [in (status-vector status)]})
                (sql/with goods (sql/fields :rest)))))

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

(defn get-event
  ([ename date] (get-event (get-eid ename date)))
  ([eid] (first (sql/select events (sql/where {:id eid}))))
  )

(defn get-price
  ([ename date]
   (:price (first (sql/select events (sql/fields :price)
                              (sql/where (and (= :date date) (= :name ename)))))))
  ([eid]
   (:price (first (sql/select events (sql/fields :price) (sql/where {:id eid}))))))

(defn get-parts
  ([ename date]
   (f/nil-fix (:rest (first (sql/select goods (sql/fields :rest)
                                        (sql/where {:events_id (get-eid ename date)}))))))
  ([eid]
   (f/nil-fix (:rest (first (sql/select goods (sql/fields :rest)
                                        (sql/where {:events_id eid})))))
   ))

(defn get-status
  ([ename date] (get-status (get-eid ename date)))
  ([eid] (:status (first (sql/select events (sql/fields :status) (sql/where {:id eid}))))))

(defn is-initial?
  ([ename date] (is-initial? (get-eid ename date)))
  ([eid]
   (= (statuses :initial)
      (:status (first (sql/select events (sql/fields :status)
                                  (sql/where {:id eid} )))))))

(defn event-from-fee [fid]
  (first (sql/select events
                     (sql/with fees
                       (sql/fields)
                       (sql/where {:id fid})))))
